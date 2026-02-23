package com.revature.pm.mapper;

import org.springframework.stereotype.Component;
import com.revature.pm.entity.VerificationCode;
import com.revature.pm.dto.VerificationCodeDTO;

@Component
public class VerificationCodeMapper {

    public VerificationCodeDTO toDTO(VerificationCode entity) {
        if (entity == null) return null;
        return new VerificationCodeDTO(entity.getCode());
    }
}