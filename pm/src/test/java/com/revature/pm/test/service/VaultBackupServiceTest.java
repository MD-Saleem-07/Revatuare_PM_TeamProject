package com.revature.pm.test.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
class VaultBackupServiceTest {

	@InjectMocks
	private VaultBackupService service;

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEntryRepository passwordEntryRepository;

	@Mock
	private ObjectMapper objectMapper;

	// ================= USER NOT FOUND =================

	@Test
	void exportVault_userNotFound() {
		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> service.exportVault(1L));
	}

	// ================= EXPORT SUCCESS =================

	@Test
	void exportVault_success() throws Exception {

		User user = new User();
		user.setId(1L);
		user.setUsername("hemanth");

		PasswordEntry entry = new PasswordEntry();
		entry.setAccountName("gmail");
		entry.setEncryptedPassword("enc1");
		entry.setLoginUsername("hemanth@gmail.com");
		entry.setCreatedAt(LocalDateTime.now());
		entry.setUpdatedAt(LocalDateTime.now());
		entry.setUser(user);

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		when(passwordEntryRepository.findByUser(user)).thenReturn(List.of(entry));

		try (MockedStatic<AESUtil> aesMock = mockStatic(AESUtil.class)) {

			aesMock.when(() -> AESUtil.decrypt("enc1")).thenReturn("Password@123");

			when(objectMapper.writeValueAsString(any())).thenReturn("json-data");

			aesMock.when(() -> AESUtil.encrypt("json-data")).thenReturn("encrypted-backup");

			String result = service.exportVault(1L);

			assertEquals("encrypted-backup", result);
		}
	}

	// ================= EXPORT EXCEPTION =================

	@Test
	void exportVault_exception() throws Exception {

		User user = new User();
		user.setId(1L);

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		when(passwordEntryRepository.findByUser(user)).thenReturn(List.of());

		when(objectMapper.writeValueAsString(any())).thenThrow(new RuntimeException("Serialization error"));

		assertThrows(RuntimeException.class, () -> service.exportVault(1L));
	}

	// ================= IMPORT SUCCESS =================

	@Test
	void importVault_success() throws Exception {

		User user = new User();
		user.setId(1L);
		user.setUsername("hemanth");

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		VaultExportDTO exportDTO = new VaultExportDTO();

		PasswordEntryDTO dto = new PasswordEntryDTO();
		dto.setAccountName("gmail");
		dto.setLoginUsername("hemanth@gmail.com");
		dto.setPassword("Password@123");

		exportDTO.setPasswords(List.of(dto));

		try (MockedStatic<AESUtil> aesMock = mockStatic(AESUtil.class)) {

			aesMock.when(() -> AESUtil.decrypt("encrypted-input")).thenReturn("json-data");

			when(objectMapper.readValue("json-data", VaultExportDTO.class)).thenReturn(exportDTO);

			aesMock.when(() -> AESUtil.encrypt("Password@123")).thenReturn("enc1");

			assertDoesNotThrow(() -> service.importVault(1L, "encrypted-input"));

			verify(passwordEntryRepository, times(1)).save(any(PasswordEntry.class));
		}
	}

	// ================= IMPORT EXCEPTION =================

	@Test
	void importVault_exception() throws Exception {

		User user = new User();
		user.setId(1L);

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		try (MockedStatic<AESUtil> aesMock = mockStatic(AESUtil.class)) {

			aesMock.when(() -> AESUtil.decrypt("bad-input")).thenThrow(new RuntimeException("Decrypt error"));

			assertThrows(RuntimeException.class, () -> service.importVault(1L, "bad-input"));
		}
	}

	// ================= USERNAME DELEGATION =================

	@Test
	void exportVaultByUsername_success() {

		User user = new User();
		user.setId(1L);
		user.setUsername("hemanth");

		when(userRepository.findByUsername("hemanth")).thenReturn(Optional.of(user));

		VaultBackupService spyService = spy(service);
		doReturn("backup-data").when(spyService).exportVault(1L);

		String result = spyService.exportVaultByUsername("hemanth");

		assertEquals("backup-data", result);
	}

	@Test
	void importVaultByUsername_success() {

		User user = new User();
		user.setId(1L);
		user.setUsername("hemanth");

		when(userRepository.findByUsername("hemanth")).thenReturn(Optional.of(user));

		VaultBackupService spyService = spy(service);

		doNothing().when(spyService).importVault(1L, "backup");

		assertDoesNotThrow(() -> spyService.importVaultByUsername("hemanth", "backup"));
	}
}
