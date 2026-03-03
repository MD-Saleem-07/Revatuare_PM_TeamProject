package com.revature.pm.test.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import com.revature.pm.dto.*;
import com.revature.pm.entity.*;
import com.revature.pm.exception.*;
import com.revature.pm.repository.*;
import com.revature.pm.security.JwtUtil;
import com.revature.pm.service.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@InjectMocks
	private AuthService authService;

	@Mock
	private UserRepository userRepository;
	@Mock
	private SecurityQuestionRepository securityQuestionRepository;
	@Mock
	private BCryptPasswordEncoder passwordEncoder;
	@Mock
	private VerificationCodeRepository verificationCodeRepository;
	@Mock
	private JwtUtil jwtUtil;

	private User user;

	@BeforeEach
	void setUp() {
		user = new User();
		user.setId(1L);
		user.setUsername("mandy");
		user.setEmail("mandy@mail.com");
		user.setMasterPassword("encoded");
		user.setTwoFactorEnabled(false);
		user.setSecurityQuestions(new ArrayList<>());
	}

	@Test
	void registerUser_success() {

		RegistrationDTO dto = new RegistrationDTO();
		dto.setUsername("mandy");
		dto.setEmail("mandy@mail.com");
		dto.setPhoneNumber("9876543210");
		dto.setMasterPassword("password");

		List<SecurityQuestionDTO> questions = List.of(
				new SecurityQuestionDTO(null, "What is your first pet name?", "dog"),
				new SecurityQuestionDTO(null, "What is your mother name?", "mom"),
				new SecurityQuestionDTO(null, "What was your first school name?", "school"));

		dto.setSecurityQuestions(questions);

		when(userRepository.existsByUsername("mandy")).thenReturn(false);
		when(userRepository.existsByEmail("mandy@mail.com")).thenReturn(false);
		when(passwordEncoder.encode(any())).thenReturn("encoded");
		when(userRepository.save(any())).thenReturn(user);

		assertDoesNotThrow(() -> authService.registerUser(dto));

		verify(userRepository, times(1)).save(any(User.class));
		verify(securityQuestionRepository, times(3)).save(any(SecurityQuestion.class));
	}

	@Test
	void registerUser_usernameExists() {
		RegistrationDTO dto = new RegistrationDTO();
		dto.setUsername("mandy");

		when(userRepository.existsByUsername("mandy")).thenReturn(true);

		assertThrows(ResourceAlreadyExistsException.class, () -> authService.registerUser(dto));
	}

	@Test
	void login_success_without2FA() {

		LoginDTO dto = new LoginDTO();
		dto.setUsernameOrEmail("mandy");
		dto.setMasterPassword("password");

		when(userRepository.findByUsername("mandy")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(any(), any())).thenReturn(true);
		when(jwtUtil.generateToken("mandy")).thenReturn("jwt-token");

		LoginResponseDTO response = authService.login(dto);

		assertEquals("SUCCESS", response.getStatus());
		assertEquals("jwt-token", response.getToken());
	}

	@Test
	void login_userNotFound() {

		LoginDTO dto = new LoginDTO();
		dto.setUsernameOrEmail("unknown");

		when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> authService.login(dto));
	}

	@Test
	void login_requiresOtp_when2FAEnabled() {

		user.setTwoFactorEnabled(true);

		LoginDTO dto = new LoginDTO();
		dto.setUsernameOrEmail("mandy");
		dto.setMasterPassword("password");

		when(userRepository.findByUsername("mandy")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(any(), any())).thenReturn(true);

		LoginResponseDTO response = authService.login(dto);

		assertEquals("OTP_REQUIRED", response.getStatus());
	}

	@Test
	void enableTwoFactor_success() {

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		authService.enableTwoFactor(1L);

		assertTrue(user.isTwoFactorEnabled());
		verify(userRepository).save(user);
	}

	@Test
	void disableTwoFactor_success() {

		user.setTwoFactorEnabled(true);
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		authService.disableTwoFactor(1L);

		assertFalse(user.isTwoFactorEnabled());
		verify(userRepository).save(user);
	}

	@Test
	void generateVerificationCode_success() {

		user.setTwoFactorEnabled(true);

		String code = authService.generateVerificationCode(user);

		assertNotNull(code);
		verify(verificationCodeRepository).save(any(VerificationCode.class));
	}

	@Test
	void generateVerificationCode_fail_if2FADisabled() {

		user.setTwoFactorEnabled(false);

		assertThrows(InvalidOperationException.class, () -> authService.generateVerificationCode(user));
	}

	@Test
	void verifyCode_success() {

		VerificationCode code = new VerificationCode();
		code.setCode("123456");
		code.setExpiryTime(LocalDateTime.now().plusSeconds(60));
		code.setUsed(false);
		code.setUser(user);

		when(userRepository.findByUsername("mandy")).thenReturn(Optional.of(user));
		when(verificationCodeRepository.findTopByUserOrderByExpiryTimeDesc(user)).thenReturn(Optional.of(code));

		assertDoesNotThrow(() -> authService.verifyCode("mandy", "123456"));

		assertTrue(code.isUsed());
	}

	@Test
	void verifyCode_invalidOtp() {

		VerificationCode code = new VerificationCode();
		code.setCode("111111");
		code.setExpiryTime(LocalDateTime.now().plusSeconds(60));
		code.setUsed(false);
		code.setUser(user);

		when(userRepository.findByUsername("mandy")).thenReturn(Optional.of(user));
		when(verificationCodeRepository.findTopByUserOrderByExpiryTimeDesc(user)).thenReturn(Optional.of(code));

		assertThrows(InvalidOperationException.class, () -> authService.verifyCode("mandy", "999999"));
	}

	@Test
	void changeMasterPassword_success() {

		ChangePasswordDTO dto = new ChangePasswordDTO();
		dto.setCurrentPassword("old");
		dto.setNewPassword("new");

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("old", "encoded")).thenReturn(true);
		when(passwordEncoder.matches("new", "encoded")).thenReturn(false);
		when(passwordEncoder.encode("new")).thenReturn("newEncoded");

		authService.changeMasterPassword(1L, dto);

		assertEquals("newEncoded", user.getMasterPassword());
		verify(userRepository).save(user);
	}

	@Test
	void getUserSecurityQuestions_success() {

		SecurityQuestion q1 = new SecurityQuestion();
		q1.setQuestion("Q1");

		user.setSecurityQuestions(List.of(q1));

		when(userRepository.findByUsernameOrEmail("mandy", "mandy")).thenReturn(Optional.of(user));

		List<String> result = authService.getUserSecurityQuestions("mandy");

		assertEquals(1, result.size());
		assertEquals("Q1", result.get(0));
	}

	@Test
	void recoverMasterPassword_success() {

		PasswordRecoveryDTO dto = new PasswordRecoveryDTO();
		dto.setUsernameOrEmail("mandy");
		dto.setNewPassword("new");

		SecurityQuestion q1 = new SecurityQuestion();
		q1.setQuestion("Q1");
		q1.setEncryptedAnswer("encoded1");

		SecurityQuestion q2 = new SecurityQuestion();
		q2.setQuestion("Q2");
		q2.setEncryptedAnswer("encoded2");

		SecurityQuestion q3 = new SecurityQuestion();
		q3.setQuestion("Q3");
		q3.setEncryptedAnswer("encoded3");

		dto.setSecurityAnswers(List.of(new SecurityQuestionDTO(null, "Q1", "answer1"),
				new SecurityQuestionDTO(null, "Q2", "answer2"), new SecurityQuestionDTO(null, "Q3", "answer3")));

		when(userRepository.findByUsername("mandy")).thenReturn(Optional.of(user));

		when(securityQuestionRepository.findByUser(user)).thenReturn(List.of(q1, q2, q3));

		when(passwordEncoder.matches("answer1", "encoded1")).thenReturn(true);
		when(passwordEncoder.matches("answer2", "encoded2")).thenReturn(true);
		when(passwordEncoder.matches("answer3", "encoded3")).thenReturn(true);

		when(passwordEncoder.encode("new")).thenReturn("newEncoded");

		authService.recoverMasterPassword(dto);

		assertEquals("newEncoded", user.getMasterPassword());
		verify(userRepository).save(user);
	}
}
