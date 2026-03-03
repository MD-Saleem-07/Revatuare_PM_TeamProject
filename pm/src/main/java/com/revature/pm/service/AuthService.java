package com.revature.pm.service;

import java.util.List;

import com.revature.pm.dto.ChangePasswordDTO;
import com.revature.pm.dto.LoginDTO;
import com.revature.pm.dto.LoginResponseDTO;
import com.revature.pm.dto.PasswordRecoveryDTO;
import com.revature.pm.dto.RegistrationDTO;
import com.revature.pm.entity.User;

public interface AuthService {

	List<String> getPredefinedQuestions();

	void registerUser(RegistrationDTO registrationDTO);

	LoginResponseDTO login(LoginDTO loginDTO);

	void enableTwoFactor(Long userId);

	void disableTwoFactor(Long userId);

	String generateVerificationCode(User user);

	String generateOperationOtp(User user);

	void verifyCode(String username, String inputCode);

	void changeMasterPassword(Long userId, ChangePasswordDTO dto);

	List<String> getUserSecurityQuestions(String usernameOrEmail);

	void recoverMasterPassword(PasswordRecoveryDTO dto);

	void enableTwoFactorByUsername(String username);

	void disableTwoFactorByUsername(String username);

	void changeMasterPasswordByUsername(String username, ChangePasswordDTO dto);
}