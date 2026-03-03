package com.revature.pm.service.impl;

import java.time.LocalDateTime;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revature.pm.dto.*;
import com.revature.pm.entity.*;
import com.revature.pm.exception.*;
import com.revature.pm.repository.*;
import com.revature.pm.security.JwtUtil;
import com.revature.pm.service.AuthService;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

	private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

	private static final List<String> PREDEFINED_QUESTIONS = List.of("What is your first pet name?",
			"What is your mother name?", "What was your first school name?", "What is your favorite movie?",
			"What city were you born in?");

	private UserRepository userRepository;
	private SecurityQuestionRepository securityQuestionRepository;
	private BCryptPasswordEncoder passwordEncoder;
	private VerificationCodeRepository verificationCodeRepository;
	private JwtUtil jwtUtil;

	public AuthServiceImpl(UserRepository userRepository, SecurityQuestionRepository securityQuestionRepository,
			BCryptPasswordEncoder passwordEncoder, VerificationCodeRepository verificationCodeRepository,
			JwtUtil jwtUtil) {
		this.userRepository = userRepository;
		this.securityQuestionRepository = securityQuestionRepository;
		this.passwordEncoder = passwordEncoder;
		this.verificationCodeRepository = verificationCodeRepository;
		this.jwtUtil = jwtUtil;
	}

	public List<String> getPredefinedQuestions() {
		return PREDEFINED_QUESTIONS;
	}

	@Transactional
	public void registerUser(RegistrationDTO registrationDTO) {

		logger.info("Registration attempt for username: {}", registrationDTO.getUsername());

		if (userRepository.existsByUsername(registrationDTO.getUsername())) {
			logger.warn("Registration failed - Username already exists: {}", registrationDTO.getUsername());
			throw new ResourceAlreadyExistsException("Username already exists!");
		}

		if (userRepository.existsByEmail(registrationDTO.getEmail())) {
			logger.warn("Registration failed - Email already exists: {}", registrationDTO.getEmail());
			throw new ResourceAlreadyExistsException("Email already exists!");
		}

		// Phone number validation (basic 10-digit check)
		if (registrationDTO.getPhoneNumber() == null || !registrationDTO.getPhoneNumber().matches("\\d{10}")) {

			logger.warn("Registration failed - Invalid phone number for username: {}", registrationDTO.getUsername());

			throw new InvalidOperationException("Phone number must be 10 digits");
		}

		if (registrationDTO.getSecurityQuestions() == null || registrationDTO.getSecurityQuestions().size() != 3) {

			logger.warn("Registration failed - Less than 3 security questions for username: {}",
					registrationDTO.getUsername());

			throw new InvalidOperationException("Exactly 3 security questions must be selected");
		}

		logger.debug("Encrypting master password for username: {}", registrationDTO.getUsername());

		String encryptedMasterPassword = passwordEncoder.encode(registrationDTO.getMasterPassword());

		User user = new User();
		user.setUsername(registrationDTO.getUsername());
		user.setEmail(registrationDTO.getEmail());
		user.setPhoneNumber(registrationDTO.getPhoneNumber());
		user.setMasterPassword(encryptedMasterPassword);
		user.setTwoFactorEnabled(false);

		User savedUser = userRepository.save(user);

		logger.info("User registered successfully with ID: {}", savedUser.getId());

		Set<String> uniqueQuestions = new HashSet<>();

		for (SecurityQuestionDTO dto : registrationDTO.getSecurityQuestions()) {

			if (!PREDEFINED_QUESTIONS.contains(dto.getQuestion())) {
				throw new InvalidOperationException("Invalid security question selected");
			}

			uniqueQuestions.add(dto.getQuestion());
		}

		// checks duplicates exist
		if (uniqueQuestions.size() < 3) {
			throw new InvalidOperationException("Security questions must be unique");
		}

		// Save questions (only after validation passed)
		for (SecurityQuestionDTO dto : registrationDTO.getSecurityQuestions()) {

			SecurityQuestion question = new SecurityQuestion();
			question.setQuestion(dto.getQuestion());
			question.setEncryptedAnswer(passwordEncoder.encode(dto.getAnswer()));
			question.setUser(savedUser);

			securityQuestionRepository.save(question);
		}

		logger.info("Security questions saved for user ID: {}", savedUser.getId());
	}

//login method
	public LoginResponseDTO login(LoginDTO loginDTO) {

		Optional<User> optionalUser;

		if (loginDTO.getUsernameOrEmail().contains("@")) {
			optionalUser = userRepository.findByEmail(loginDTO.getUsernameOrEmail());
		} else {
			optionalUser = userRepository.findByUsername(loginDTO.getUsernameOrEmail());
		}

		if (!optionalUser.isPresent()) {
			throw new ResourceNotFoundException("User not found!");
		}

		User user = optionalUser.get();

		boolean passwordMatches = passwordEncoder.matches(loginDTO.getMasterPassword(), user.getMasterPassword());

		if (!passwordMatches) {
			throw new InvalidOperationException("Invalid password");
		}

		if (user.isTwoFactorEnabled()) {

			generateVerificationCode(user);

			return new LoginResponseDTO("OTP_REQUIRED", null);
		}

		String token = jwtUtil.generateToken(user.getUsername());

		return new LoginResponseDTO("SUCCESS", token);
	}

	public void enableTwoFactor(Long userId) {

		logger.info("Enabling 2FA for user ID: {}", userId);

		User user = userRepository.findById(userId).orElseThrow(() -> {
			logger.error("Enable 2FA failed - User not found: {}", userId);
			return new ResourceNotFoundException("User not found");
		});

		user.setTwoFactorEnabled(true);
		userRepository.save(user);

		logger.info("2FA enabled successfully for user ID: {}", userId);
	}

	public void disableTwoFactor(Long userId) {

		logger.info("Disabling 2FA for user ID: {}", userId);

		User user = userRepository.findById(userId).orElseThrow(() -> {
			logger.error("Disable 2FA failed - User not found: {}", userId);
			return new ResourceNotFoundException("User not found");
		});

		user.setTwoFactorEnabled(false);
		userRepository.save(user);

		logger.info("2FA disabled successfully for user ID: {}", userId);
	}

	public String generateVerificationCode(User user) {

		logger.info("Generating verification code for user: {}", user.getUsername());

		if (!user.isTwoFactorEnabled()) {
			logger.warn("Verification code generation failed - 2FA not enabled for user: {}", user.getUsername());
			throw new InvalidOperationException("2FA is not enabled for this user");
		}

		Random random = new Random();
		int codeNumber = 100000 + random.nextInt(899999);
		String code = String.valueOf(codeNumber);

		VerificationCode verificationCode = new VerificationCode();
		verificationCode.setCode(code);
		verificationCode.setExpiryTime(LocalDateTime.now().plusSeconds(90));
		verificationCode.setUsed(false);
		verificationCode.setUser(user);

		verificationCodeRepository.save(verificationCode);

		logger.info("Verification code generated successfully for user: {}", user.getUsername());

		return code;
	}

	public String generateOperationOtp(User user) {
		return generateVerificationCode(user);
	}

	@Transactional
	public void verifyCode(String username, String inputCode) {

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		VerificationCode verificationCode = verificationCodeRepository.findTopByUserOrderByExpiryTimeDesc(user)
				.orElseThrow(() -> new InvalidOperationException("No OTP found"));

		if (verificationCode.isUsed()) {
			throw new InvalidOperationException("OTP already used");
		}

		if (verificationCode.getExpiryTime().isBefore(LocalDateTime.now())) {
			throw new InvalidOperationException("OTP expired");
		}

		if (!verificationCode.getCode().equals(inputCode)) {
			throw new InvalidOperationException("Invalid OTP");
		}

		verificationCode.setUsed(true);
		verificationCodeRepository.save(verificationCode);

		logger.info("OTP verified successfully for user: {}", username);
	}

	@Transactional
	public void changeMasterPassword(Long userId, ChangePasswordDTO dto) {

		logger.info("User {} attempting to change master password", userId);

		User user = userRepository.findById(userId).orElseThrow(() -> {
			logger.error("Change password failed - User not found: {}", userId);
			return new ResourceNotFoundException("User not found");
		});

		boolean matches = passwordEncoder.matches(dto.getCurrentPassword(), user.getMasterPassword());

		if (!matches) {
			logger.warn("Change password failed - Incorrect current password for user ID: {}", userId);
			throw new InvalidOperationException("Current password is incorrect");
		}

		if (passwordEncoder.matches(dto.getNewPassword(), user.getMasterPassword())) {

			logger.warn("Change password failed - New password same as old for user ID: {}", userId);
			throw new InvalidOperationException("New password cannot be same as old password");
		}

		String encryptedNewPassword = passwordEncoder.encode(dto.getNewPassword());

		user.setMasterPassword(encryptedNewPassword);
		userRepository.save(user);

		logger.info("Master password changed successfully for user ID: {}", userId);
	}

	public List<String> getUserSecurityQuestions(String usernameOrEmail) {

		User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		return user.getSecurityQuestions().stream().map(SecurityQuestion::getQuestion).toList();
	}

	@Transactional
	public void recoverMasterPassword(PasswordRecoveryDTO dto) {

		logger.info("Password recovery attempt for: {}", dto.getUsernameOrEmail());

		User user;

		if (dto.getUsernameOrEmail().contains("@")) {
			user = userRepository.findByEmail(dto.getUsernameOrEmail()).orElseThrow(() -> {
				logger.warn("Recovery failed - User not found: {}", dto.getUsernameOrEmail());
				return new ResourceNotFoundException("User not found");
			});
		} else {
			user = userRepository.findByUsername(dto.getUsernameOrEmail()).orElseThrow(() -> {
				logger.warn("Recovery failed - User not found: {}", dto.getUsernameOrEmail());
				return new ResourceNotFoundException("User not found");
			});
		}

		List<SecurityQuestion> storedQuestions = securityQuestionRepository.findByUser(user);

		if (storedQuestions.size() < 3) {
			logger.error("Recovery failed - Security questions not configured properly for user: {}",
					user.getUsername());
			throw new InvalidOperationException("Security questions not properly configured");
		}

		for (SecurityQuestion stored : storedQuestions) {

			boolean matched = false;

			for (SecurityQuestionDTO input : dto.getSecurityAnswers()) {

				if (stored.getQuestion().equalsIgnoreCase(input.getQuestion())) {

					boolean answerMatches = passwordEncoder.matches(input.getAnswer(), stored.getEncryptedAnswer());

					if (!answerMatches) {
						logger.warn("Recovery failed - Incorrect security answer for user: {}", user.getUsername());
						throw new InvalidOperationException("Incorrect security answer");
					}

					matched = true;
					break;
				}
			}

			if (!matched) {
				logger.warn("Recovery failed - Missing security answer for user: {}", user.getUsername());
				throw new InvalidOperationException("Missing security answer");
			}
		}

		String encryptedPassword = passwordEncoder.encode(dto.getNewPassword());

		user.setMasterPassword(encryptedPassword);
		userRepository.save(user);

		logger.info("Password recovered successfully for user: {}", user.getUsername());
	}

	public void enableTwoFactorByUsername(String username) {

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		enableTwoFactor(user.getId());
	}

	public void disableTwoFactorByUsername(String username) {

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		disableTwoFactor(user.getId());
	}

	@Transactional
	public void changeMasterPasswordByUsername(String username, ChangePasswordDTO dto) {

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		changeMasterPassword(user.getId(), dto);
	}
}