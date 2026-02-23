package com.revature.pm.dto;

import java.util.List;

public class VaultExportDTO {

    private String username;
    private List<PasswordEntryDTO> passwords;

    public VaultExportDTO() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<PasswordEntryDTO> getPasswords() {
        return passwords;
    }

    public void setPasswords(List<PasswordEntryDTO> passwords) {
        this.passwords = passwords;
    }
}