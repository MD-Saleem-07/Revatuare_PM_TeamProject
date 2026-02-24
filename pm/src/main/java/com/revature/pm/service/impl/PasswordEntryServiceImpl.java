package com.revature.pm.service.impl;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revature.pm.dto.*;
import com.revature.pm.entity.PasswordEntry;
import com.revature.pm.entity.User;
import com.revature.pm.exception.InvalidOperationException;
import com.revature.pm.exception.ResourceNotFoundException;
import com.revature.pm.repository.PasswordEntryRepository;
import com.revature.pm.repository.UserRepository;
import com.revature.pm.service.PasswordEntryService;
import com.revature.pm.util.AESUtil;

@Service
public class PasswordEntryServiceImpl implements PasswordEntryService {
	
	
	@Autowired
	private PasswordEntryRepository passwordEntryRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	private static final Logger logger = LoggerFactory.getLogger(PasswordEntryService.class);

	private User getUserByUsername(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
	}

	private PasswordEntry getEntryAndValidate(User user, Long entryId) {
		PasswordEntry entry = passwordEntryRepository.findById(entryId)
				.orElseThrow(() -> new ResourceNotFoundException("Password entry not found"));

		if (!entry.getUser().getId().equals(user.getId())) {
			throw new RuntimeException("Unauthorized access");
		}

		return entry;
	}
	@Override
	@Transactional
	public void addPasswordByUsername(String username, PasswordEntryDTO dto) {

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
	}
	@Override
	public List<PasswordEntryDTO> getAllPasswordsByUsername(String username) {

		User user = getUserByUsername(username);

		return passwordEntryRepository.findByUser(user).stream().map(this::mapToDTO).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void updatePasswordByUsername(String username, Long entryId, PasswordEntryDTO dto) {

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
	}
	
	@Override
	@Transactional
	public void deletePasswordByUsername(String username, Long entryId) {

		User user = getUserByUsername(username);
		PasswordEntry entry = getEntryAndValidate(user, entryId);

		passwordEntryRepository.delete(entry);
	}
	
	@Override
	@Transactional
	public void toggleFavoriteByUsername(String username, Long entryId) {

		User user = getUserByUsername(username);
		PasswordEntry entry = getEntryAndValidate(user, entryId);

		entry.setFavorite(!entry.isFavorite());
		entry.setUpdatedAt(LocalDateTime.now());

		passwordEntryRepository.save(entry);
	}
	@Override
	public List<PasswordEntryDTO> getFavoritePasswordsByUsername(String username) {

		User user = getUserByUsername(username);

		return passwordEntryRepository.findByUserAndFavoriteTrue(user).stream().map(this::mapToDTO)
				.collect(Collectors.toList());
	}
	
	@Override
	public String viewPasswordByUsername(String username, Long entryId, ViewPasswordDTO dto) {

		User user = getUserByUsername(username);
		PasswordEntry entry = getEntryAndValidate(user, entryId);

		if (!passwordEncoder.matches(dto.getMasterPassword(), user.getMasterPassword())) {
			throw new InvalidOperationException("wrong master password");
		}
		if (dto == null || dto.getMasterPassword() == null) {
			throw new InvalidOperationException("master password required");
		}
		return AESUtil.decrypt(entry.getEncryptedPassword());
	}
	
	@Override
	public List<PasswordEntryDTO> searchPasswordsByUsername(String username, String keyword) {

		User user = getUserByUsername(username);

		List<PasswordEntry> results = passwordEntryRepository.findByUserAndAccountNameContainingIgnoreCase(user,
				keyword);

		if (results.isEmpty()) {
			results = passwordEntryRepository.findByUserAndLoginUsernameContainingIgnoreCase(user, keyword);
		}

		if (results.isEmpty()) {
			results = passwordEntryRepository.findByUserAndWebsiteUrlContainingIgnoreCase(user, keyword);
		}

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

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		List<PasswordEntry> entries = passwordEntryRepository.findByUser(user);

		DashboardStatsDTO stats = new DashboardStatsDTO();

		stats.setTotalPasswords(entries.size());

		int strong = 0;
		int weak = 0;

		Map<String, Integer> frequency = new HashMap<>();

		for (PasswordEntry entry : entries) {

			String decrypted = AESUtil.decrypt(entry.getEncryptedPassword());

			if (decrypted.length() >= 12) {
				strong++;
			} else {
				weak++;
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

		return stats;
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
