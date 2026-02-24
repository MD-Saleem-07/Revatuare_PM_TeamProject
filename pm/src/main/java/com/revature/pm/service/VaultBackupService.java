package com.revature.pm.service;

public interface VaultBackupService {

    String exportVault(Long userId);

    void importVault(Long userId, String encryptedBackup);

    String exportVaultByUsername(String username);

    void importVaultByUsername(String username, String encryptedBackup);
    
    
}