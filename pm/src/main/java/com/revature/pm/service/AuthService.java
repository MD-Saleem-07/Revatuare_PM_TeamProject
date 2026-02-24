package com.revature.pm.service;

import java.util.List;

import com.revature.pm.dto.*;

public interface AuthService {

    List<String> getPredefinedQuestions();

    void registerUser(RegistrationDTO registrationDTO);

    LoginResponseDTO login(LoginDTO loginDTO);

    void enableTwoFactor(Long userId);

    void disableTwoFactor(Long userId);

    void verifyCode(String username, String inputCode);

    void changeMasterPassword(Long userId, ChangePasswordDTO dto);

    List<String> getUserSecurityQuestions(String usernameOrEmail);

    void recoverMasterPassword(PasswordRecoveryDTO dto);

    void enableTwoFactorByUsername(String username);

    void disableTwoFactorByUsername(String username);

    void changeMasterPasswordByUsername(String username, ChangePasswordDTO dto);
}