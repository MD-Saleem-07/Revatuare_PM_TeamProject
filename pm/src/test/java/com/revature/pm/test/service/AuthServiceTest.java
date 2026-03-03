package com.revature.pm.test.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.revature.pm.dto.*;
import com.revature.pm.entity.*;
import com.revature.pm.exception.*;
import com.revature.pm.repository.*;
import com.revature.pm.security.JwtUtil;
import com.revature.pm.service.impl.AuthServiceImpl;

class AuthServiceImplTest {

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

	@InjectMocks
	private AuthServiceImpl authService;

	private User user;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		user = new User();
		user.setId(1L);
		user.setUsername("mandy");
		user.setEmail("mandy@test.com");
		user.setMasterPassword("encoded");
		user.setTwoFactorEnabled(false);
	}

	@Test
	void registerUser_success() {

		RegistrationDTO dto = validRegistration();

		when(userRepository.existsByUsername(any())).thenReturn(false);
		when(userRepository.existsByEmail(any())).thenReturn(false);
		when(passwordEncoder.encode(any())).thenReturn("encoded");
		when(userRepository.save(any())).thenReturn(user);

		authService.registerUser(dto);

		verify(userRepository).save(any(User.class));
		verify(securityQuestionRepository, times(3)).save(any());
	}

	@Test
	void registerUser_usernameAlreadyExists() {

		RegistrationDTO dto = validRegistration();

		when(userRepository.existsByUsername("mandy")).thenReturn(true);

		assertThrows(ResourceAlreadyExistsException.class, () -> authService.registerUser(dto));
	}

	@Test
	void registerUser_invalidPhoneNumber() {

		RegistrationDTO dto = validRegistration();
		dto.setPhoneNumber("123");

		when(userRepository.existsByUsername(any())).thenReturn(false);
		when(userRepository.existsByEmail(any())).thenReturn(false);

		assertThrows(InvalidOperationException.class, () -> authService.registerUser(dto));
	}

	@Test
	void login_success_without2FA() {

		LoginDTO dto = new LoginDTO();
		dto.setUsernameOrEmail("mandy");
		dto.setMasterPassword("correct");

		when(userRepository.findByUsername("mandy")).thenReturn(Optional.of(user));

		when(passwordEncoder.matches("correct", "encoded")).thenReturn(true);

		when(jwtUtil.generateToken("mandy")).thenReturn("jwt-token");

		LoginResponseDTO response = authService.login(dto);

		assertEquals("SUCCESS", response.getStatus());
		assertEquals("jwt-token", response.getToken());
	}

	@Test
	void login_wrongPassword() {

		LoginDTO dto = new LoginDTO();
		dto.setUsernameOrEmail("mandy");
		dto.setMasterPassword("wrong");

		when(userRepository.findByUsername("mandy")).thenReturn(Optional.of(user));

		when(passwordEncoder.matches(any(), any())).thenReturn(false);

		assertThrows(InvalidOperationException.class, () -> authService.login(dto));
	}

	@Test
	void login_userNotFound() {

		LoginDTO dto = new LoginDTO();
		dto.setUsernameOrEmail("unknown");

		when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> authService.login(dto));
	}

	@Test
	void enableTwoFactor_success() {

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		authService.enableTwoFactor(1L);

		assertTrue(user.isTwoFactorEnabled());
		verify(userRepository).save(user);
	}

	@Test
	void generateVerificationCode_success() {

		user.setTwoFactorEnabled(true);

		String code = authService.generateVerificationCode(user);

		assertNotNull(code);
		assertEquals(6, code.length());
		verify(verificationCodeRepository).save(any());
	}

	@Test
	void generateVerificationCode_whenDisabled() {

		user.setTwoFactorEnabled(false);

		assertThrows(InvalidOperationException.class, () -> authService.generateVerificationCode(user));
	}

	@Test
	void verifyCode_success() {

		VerificationCode vc = new VerificationCode();
		vc.setCode("123456");
		vc.setExpiryTime(LocalDateTime.now().plusSeconds(60));
		vc.setUsed(false);
		vc.setUser(user);

		when(userRepository.findByUsername("mandy")).thenReturn(Optional.of(user));

		when(verificationCodeRepository.findTopByUserOrderByExpiryTimeDesc(user)).thenReturn(Optional.of(vc));

		authService.verifyCode("mandy", "123456");

		assertTrue(vc.isUsed());
		verify(verificationCodeRepository).save(vc);
	}

	@Test
	void verifyCode_expired() {

		VerificationCode vc = new VerificationCode();
		vc.setCode("123456");
		vc.setExpiryTime(LocalDateTime.now().minusSeconds(10));
		vc.setUsed(false);
		vc.setUser(user);

		when(userRepository.findByUsername("mandy")).thenReturn(Optional.of(user));

		when(verificationCodeRepository.findTopByUserOrderByExpiryTimeDesc(user)).thenReturn(Optional.of(vc));

		assertThrows(InvalidOperationException.class, () -> authService.verifyCode("mandy", "123456"));
	}

	@Test
	void changePassword_success() {

		ChangePasswordDTO dto = new ChangePasswordDTO();
		dto.setCurrentPassword("correct");
		dto.setNewPassword("newPass");

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		when(passwordEncoder.matches("correct", "encoded")).thenReturn(true);

		when(passwordEncoder.matches("newPass", "encoded")).thenReturn(false);

		when(passwordEncoder.encode("newPass")).thenReturn("newEncoded");

		authService.changeMasterPassword(1L, dto);

		verify(userRepository).save(user);
	}

	private RegistrationDTO validRegistration() {

		RegistrationDTO dto = new RegistrationDTO();
		dto.setUsername("mandy");
		dto.setEmail("mandy@test.com");
		dto.setPhoneNumber("9876543210");
		dto.setMasterPassword("password");

		SecurityQuestionDTO q1 = new SecurityQuestionDTO();
		q1.setQuestion("What is your first pet name?");
		q1.setAnswer("dog");

		SecurityQuestionDTO q2 = new SecurityQuestionDTO();
		q2.setQuestion("What is your mother name?");
		q2.setAnswer("mom");

		SecurityQuestionDTO q3 = new SecurityQuestionDTO();
		q3.setQuestion("What was your first school name?");
		q3.setAnswer("school");

		dto.setSecurityQuestions(List.of(q1, q2, q3));

		return dto;
	}
}