package com.revature.pm.service;

import com.revature.pm.dto.LoginDTO;
import com.revature.pm.dto.RegistrationDTO;

public interface AuthService {

    void register(RegistrationDTO dto);

    String login(LoginDTO dto);
}