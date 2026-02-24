package com.revature.pm.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.pm.dto.PasswordEntryDTO;
import com.revature.pm.dto.VaultExportDTO;
import com.revature.pm.entity.PasswordEntry;
import com.revature.pm.entity.User;
import com.revature.pm.exception.ResourceNotFoundException;
import com.revature.pm.repository.PasswordEntryRepository;
import com.revature.pm.repository.UserRepository;
import com.revature.pm.service.VaultBackupService;
import com.revature.pm.util.AESUtil;
import tools.jackson.databind.ObjectMapper;

@Service
public class VaultBackupServiceImpl implements VaultBackupService {
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEntryRepository passwordEntryRepository;

	@Autowired
	private ObjectMapper objectMapper;

	private static final Logger logger = LoggerFactory.getLogger(VaultBackupService.class);
	
	@Override
	public String exportVault(Long userId) {

		logger.info("Vault export requested for user {}", userId);

		User user = userRepository.findById(userId).orElseThrow(() -> {
			logger.error("Vault export failed - User not found: {}", userId);
			return new ResourceNotFoundException("User not found");
		});

		List<PasswordEntry> entries = passwordEntryRepository.findByUser(user);

		logger.debug("Total entries to export for user {}: {}", userId, entries.size());

		List<PasswordEntryDTO> dtoList = entries.stream().map(entry -> {

			PasswordEntryDTO dto = new PasswordEntryDTO();

			dto.setAccountName(entry.getAccountName());
			dto.setWebsiteUrl(entry.getWebsiteUrl());
			dto.setLoginUsername(entry.getLoginUsername());

			dto.setPassword(AESUtil.decrypt(entry.getEncryptedPassword()));
			dto.setCategory(entry.getCategory());
			dto.setNotes(entry.getNotes());

			return dto;

		}).collect(Collectors.toList());

		VaultExportDTO exportDTO = new VaultExportDTO();
		exportDTO.setUsername(user.getUsername());
		exportDTO.setPasswords(dtoList);

		try {

			String json = objectMapper.writeValueAsString(exportDTO);

			String encryptedBackup = AESUtil.encrypt(json);

			logger.info("Vault export completed successfully for user {} ({} entries)", userId, entries.size());

			return encryptedBackup;

		} catch (Exception e) {

			logger.error("Vault export failed for user {} due to error", userId, e);

			throw new RuntimeException("Error while exporting vault");
		}
	}
	@Override
	@Transactional
	public void importVault(Long userId, String encryptedBackup) {

		logger.info("Vault import requested for user {}", userId);

		User user = userRepository.findById(userId).orElseThrow(() -> {
			logger.error("Vault import failed - User not found: {}", userId);
			return new ResourceNotFoundException("User not found");
		});

		try {

			String decryptedJson = AESUtil.decrypt(encryptedBackup);

			VaultExportDTO exportDTO = objectMapper.readValue(decryptedJson, VaultExportDTO.class);

			int importCount = 0;

			for (PasswordEntryDTO dto : exportDTO.getPasswords()) {

				PasswordEntry entry = new PasswordEntry();

				entry.setAccountName(dto.getAccountName());
				entry.setWebsiteUrl(dto.getWebsiteUrl());
				entry.setLoginUsername(dto.getLoginUsername());

				entry.setEncryptedPassword(AESUtil.encrypt(dto.getPassword()));

				entry.setCategory(dto.getCategory());
				entry.setNotes(dto.getNotes());
				entry.setFavorite(false);
				entry.setCreatedAt(LocalDateTime.now());
				entry.setUpdatedAt(LocalDateTime.now());
				entry.setUser(user);

				passwordEntryRepository.save(entry);
				importCount++;
			}

			logger.info("Vault import completed successfully for user {} ({} entries imported)", userId, importCount);

		} catch (Exception e) {

			logger.error("Vault import failed for user {} due to error", userId, e);

			throw new RuntimeException("Error while importing vault");
		}
	}
	@Override
	public String exportVaultByUsername(String username) {

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		return exportVault(user.getId());
	}

	@Override
	@Transactional
	public void importVaultByUsername(String username, String encryptedBackup) {

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		importVault(user.getId(), encryptedBackup);
	}
}