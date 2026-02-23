package com.revature.pm.mapper;

import org.springframework.stereotype.Component;
import com.revature.pm.entity.PasswordEntry;
import com.revature.pm.dto.PasswordEntryDTO;

@Component
public class PasswordEntryMapper {

    public PasswordEntryDTO toDTO(PasswordEntry entity, String decryptedPassword) {
        if (entity == null) return null;

        PasswordEntryDTO dto = new PasswordEntryDTO();
        dto.setId(entity.getId());
        dto.setAccountName(entity.getAccountName());
        dto.setWebsiteUrl(entity.getWebsiteUrl());
        dto.setLoginUsername(entity.getLoginUsername());
        dto.setPassword(decryptedPassword);
        dto.setCategory(entity.getCategory());
        dto.setNotes(entity.getNotes());
        dto.setFavorite(entity.isFavorite());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }
}