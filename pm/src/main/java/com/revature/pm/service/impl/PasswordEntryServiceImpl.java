package com.revature.pm.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revature.pm.dto.*;
import com.revature.pm.entity.*;
import com.revature.pm.exception.*;
import com.revature.pm.repository.*;
import com.revature.pm.service.PasswordEntryService;
import com.revature.pm.util.*;

@Service
@Transactional
public class PasswordEntryServiceImpl implements PasswordEntryService {

    private static final Logger logger =
            LoggerFactory.getLogger(PasswordEntryServiceImpl.class);

    private PasswordEntryRepository passwordEntryRepository;
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    public PasswordEntryServiceImpl(
            PasswordEntryRepository passwordEntryRepository,
            UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder) {

        this.passwordEntryRepository = passwordEntryRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
     
	private User getUserByUsername(String username) {
		logger.debug("Fetching user by username: {}", username);

		return userRepository.findByUsername(username).orElseThrow(() -> {
			logger.error("User not found: {}", username);
			return new ResourceNotFoundException("User not found");
		});
	}

	private PasswordEntry getEntryAndValidate(User user, Long entryId) {

		logger.debug("Validating entry {} for user {}", entryId, user.getUsername());

		PasswordEntry entry = passwordEntryRepository.findById(entryId).orElseThrow(() -> {
			logger.error("Password entry not found: {}", entryId);
			return new ResourceNotFoundException("Password entry not found");
		});

		if (!entry.getUser().getId().equals(user.getId())) {
			logger.warn("Unauthorized access attempt by user {} for entry {}", user.getUsername(), entryId);
			throw new RuntimeException("Unauthorized access");
		}

		return entry;
	}
	@Override
	@Transactional
	public void addPasswordByUsername(String username, PasswordEntryDTO dto) {

		logger.info("Adding password entry for user: {}", username);

		User user = getUserByUsername(username);

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

		logger.info("Password entry added successfully for user: {}", username);
	}
	
	@Override
	public List<PasswordEntryDTO> getAllPasswordsByUsername(String username) {

		logger.info("Fetching all passwords for user: {}", username);

		User user = getUserByUsername(username);

		List<PasswordEntryDTO> result = passwordEntryRepository.findByUser(user).stream().map(this::mapToDTO)
				.collect(Collectors.toList());

		logger.debug("Total passwords fetched for {}: {}", username, result.size());

		return result;
	}
	
	@Override
	@Transactional
	public void updatePasswordByUsername(String username, Long entryId, PasswordEntryDTO dto) {

		logger.info("Updating password entry {} for user {}", entryId, username);

		User user = getUserByUsername(username);
		PasswordEntry entry = getEntryAndValidate(user, entryId);

		entry.setAccountName(dto.getAccountName());
		entry.setWebsiteUrl(dto.getWebsiteUrl());
		entry.setLoginUsername(dto.getLoginUsername());
		entry.setEncryptedPassword(AESUtil.encrypt(dto.getPassword()));
		entry.setCategory(dto.getCategory());
		entry.setNotes(dto.getNotes());
		entry.setUpdatedAt(LocalDateTime.now());

		passwordEntryRepository.save(entry);

		logger.info("Password entry {} updated successfully for user {}", entryId, username);
	}

	@Override
	@Transactional
	public void deletePasswordByUsername(String username, Long entryId) {

		logger.info("Deleting password entry {} for user {}", entryId, username);

		User user = getUserByUsername(username);
		PasswordEntry entry = getEntryAndValidate(user, entryId);

		passwordEntryRepository.delete(entry);

		logger.info("Password entry {} deleted successfully for user {}", entryId, username);
	}

	@Override
	@Transactional
	public void toggleFavoriteByUsername(String username, Long entryId) {

		logger.info("Toggling favorite for entry {} for user {}", entryId, username);

		User user = getUserByUsername(username);
		PasswordEntry entry = getEntryAndValidate(user, entryId);

		entry.setFavorite(!entry.isFavorite());
		entry.setUpdatedAt(LocalDateTime.now());

		passwordEntryRepository.save(entry);

		logger.debug("Favorite status toggled for entry {}", entryId);
	}

	@Override
	public List<PasswordEntryDTO> getFavoritePasswordsByUsername(String username) {

		User user = getUserByUsername(username);

		return passwordEntryRepository.findByUserAndFavoriteTrue(user).stream().map(this::mapToDTO)
				.collect(Collectors.toList());
	}
	
	@Override
	public String viewPasswordByUsername(String username, Long entryId, ViewPasswordDTO dto) {

		logger.info("Password view requested for entry {} by user {}", entryId, username);

		User user = getUserByUsername(username);
		PasswordEntry entry = getEntryAndValidate(user, entryId);

		if (dto == null || dto.getMasterPassword() == null) {
			logger.warn("Master password missing for user {}", username);
			throw new InvalidOperationException("master password required");
		}

		if (!passwordEncoder.matches(dto.getMasterPassword(), user.getMasterPassword())) {
			logger.warn("Wrong master password attempt by user {}", username);
			throw new InvalidOperationException("wrong master password");
		}

		logger.info("Password decrypted successfully for entry {}", entryId);

		return AESUtil.decrypt(entry.getEncryptedPassword());
	}

	@Override
	public List<PasswordEntryDTO> searchPasswordsByUsername(String username, String keyword) {

		logger.info("Searching passwords for user {} with keyword '{}'", username, keyword);

		User user = getUserByUsername(username);

		List<PasswordEntry> results = passwordEntryRepository.findByUserAndAccountNameContainingIgnoreCase(user,
				keyword);

		if (results.isEmpty()) {
			results = passwordEntryRepository.findByUserAndLoginUsernameContainingIgnoreCase(user, keyword);
		}

		if (results.isEmpty()) {
			results = passwordEntryRepository.findByUserAndWebsiteUrlContainingIgnoreCase(user, keyword);
		}

		logger.debug("Search results count: {}", results.size());

		return results.stream().map(this::mapToDTO).collect(Collectors.toList());
	}
	
	@Override
	public List<PasswordEntryDTO> filterByCategoryByUsername(String username, String category) {

		User user = getUserByUsername(username);

		return passwordEntryRepository.findByUserAndCategoryIgnoreCase(user, category).stream().map(this::mapToDTO)
				.collect(Collectors.toList());
	}

	@Override
	public List<PasswordEntryDTO> sortPasswordsByUsername(String username, String sortBy) {

		User user = getUserByUsername(username);

		Sort sort;

		switch (sortBy.toLowerCase()) {
		case "name":
			sort = Sort.by("accountName").ascending();
			break;
		case "created":
			sort = Sort.by("createdAt").descending();
			break;
		case "modified":
			sort = Sort.by("updatedAt").descending();
			break;
		default:
			sort = Sort.by("createdAt").descending();
		}

		return passwordEntryRepository.findByUser(user, sort).stream().map(this::mapToDTO).collect(Collectors.toList());
	}
	
	@Override
	public DashboardStatsDTO getDashboardStatsByUsername(String username) {

		logger.info("Generating dashboard stats for user {}", username);

		User user = userRepository.findByUsername(username).orElseThrow(() -> {
			logger.error("User not found for dashboard stats: {}", username);
			return new ResourceNotFoundException("User not found");
		});

		List<PasswordEntry> entries = passwordEntryRepository.findByUser(user);

		logger.debug("Total entries for stats: {}", entries.size());

		DashboardStatsDTO stats = new DashboardStatsDTO();

		stats.setTotalPasswords(entries.size());

		int strong = 0;
		int weak = 0;

		Map<String, Integer> frequency = new HashMap<>();

		for (PasswordEntry entry : entries) {

			String decrypted = AESUtil.decrypt(entry.getEncryptedPassword());

			String strength = PasswordGeneratorUtil.checkStrength(decrypted);

			if ("Weak".equals(strength)) {
				weak++;
			} else {
				strong++;
			}

			frequency.put(decrypted, frequency.getOrDefault(decrypted, 0) + 1);
		}

		int reused = 0;

		for (Integer count : frequency.values()) {
			if (count > 1) {
				reused += count;
			}
		}

		stats.setStrongPasswords(strong);
		stats.setWeakPasswords(weak);
		stats.setReusedPasswords(reused);
		logger.info("Dashboard stats generated successfully for user {}", username);
		return stats;
	}

	// new ly added for pagination
	@Override
	public Page<PasswordEntryDTO> getPasswordsPageByUsername(String username, int page, int size) {

		logger.info("Fetching paginated passwords for user: {}, page: {}", username, page);

		User user = getUserByUsername(username);

		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

		Page<PasswordEntry> pageResult = passwordEntryRepository.findByUser(user, pageable);

		return pageResult.map(this::mapToDTO);
	}

	private PasswordEntryDTO mapToDTO(PasswordEntry entry) {

		PasswordEntryDTO dto = new PasswordEntryDTO();

		dto.setId(entry.getId());
		dto.setAccountName(entry.getAccountName());
		dto.setWebsiteUrl(entry.getWebsiteUrl());
		dto.setLoginUsername(entry.getLoginUsername());
		dto.setPassword("********");
		dto.setCategory(entry.getCategory());
		dto.setNotes(entry.getNotes());
		dto.setFavorite(entry.isFavorite());
		dto.setCreatedAt(entry.getCreatedAt());
		dto.setUpdatedAt(entry.getUpdatedAt());

		return dto;
	}
}