package com.revature.pm.test.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import tools.jackson.databind.ObjectMapper;
import com.revature.pm.dto.VaultExportDTO;
import com.revature.pm.entity.PasswordEntry;
import com.revature.pm.entity.User;
import com.revature.pm.exception.ResourceNotFoundException;
import com.revature.pm.repository.PasswordEntryRepository;
import com.revature.pm.repository.UserRepository;
import com.revature.pm.service.impl.VaultBackupServiceImpl;
import com.revature.pm.util.AESUtil;

class VaultBackupServiceImplTest {

	@Mock
	private UserRepository userRepository;
	@Mock
	private PasswordEntryRepository passwordEntryRepository;
	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private VaultBackupServiceImpl vaultService;

	private User user;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);

		user = new User();
		user.setId(1L);
		user.setUsername("testuser");
	}

	@Test
	void exportVault_userNotFound() {

		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> vaultService.exportVault(1L));
	}

	@Test
	void exportVault_success() throws Exception {

		PasswordEntry entry = new PasswordEntry();
		entry.setAccountName("Google");
		entry.setEncryptedPassword(AESUtil.encrypt("abc123"));
		entry.setUser(user);

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		when(passwordEntryRepository.findByUser(user)).thenReturn(List.of(entry));

		when(objectMapper.writeValueAsString(any(VaultExportDTO.class))).thenReturn("{\"mock\":\"json\"}");

		String result = vaultService.exportVault(1L);

		assertNotNull(result);
		verify(objectMapper).writeValueAsString(any(VaultExportDTO.class));
	}

	@Test
	void exportVault_objectMapperThrows() throws Exception {

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		when(passwordEntryRepository.findByUser(user)).thenReturn(List.of());

		when(objectMapper.writeValueAsString(any())).thenThrow(new RuntimeException());

		assertThrows(RuntimeException.class, () -> vaultService.exportVault(1L));
	}

	@Test
	void importVault_userNotFound() {

		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> vaultService.importVault(1L, "data"));
	}

	@Test
	void importVault_success() throws Exception {

		String json = "{\"username\":\"testuser\",\"passwords\":[]}";

		String encryptedBackup = AESUtil.encrypt(json);

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		VaultExportDTO exportDTO = new VaultExportDTO();
		exportDTO.setUsername("testuser");
		exportDTO.setPasswords(List.of());

		when(objectMapper.readValue(anyString(), eq(VaultExportDTO.class))).thenReturn(exportDTO);

		vaultService.importVault(1L, encryptedBackup);

		verify(passwordEntryRepository, never()).save(any());
	}

	@Test
	void importVault_objectMapperThrows() throws Exception {

		String encryptedBackup = AESUtil.encrypt("bad-json");

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		when(objectMapper.readValue(anyString(), eq(VaultExportDTO.class))).thenThrow(new RuntimeException());

		assertThrows(RuntimeException.class, () -> vaultService.importVault(1L, encryptedBackup));
	}

	@Test
	void exportVaultByUsername_success() {

		when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		when(passwordEntryRepository.findByUser(user)).thenReturn(List.of());

		when(objectMapper.writeValueAsString(any())).thenReturn("{}");

		String result = vaultService.exportVaultByUsername("testuser");

		assertNotNull(result);
	}

}