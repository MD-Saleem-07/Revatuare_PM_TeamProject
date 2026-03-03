package com.revature.pm.test.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import com.revature.pm.dto.AuditReportDTO;
import com.revature.pm.entity.PasswordEntry;
import com.revature.pm.entity.User;
import com.revature.pm.exception.ResourceNotFoundException;
import com.revature.pm.repository.PasswordEntryRepository;
import com.revature.pm.repository.UserRepository;
import com.revature.pm.service.impl.SecurityAuditServiceImpl;
import com.revature.pm.util.AESUtil;

class SecurityAuditServiceImplTest {

	@Mock
	private UserRepository userRepository;
	@Mock
	private PasswordEntryRepository passwordEntryRepository;

	@InjectMocks
	private SecurityAuditServiceImpl auditService;

	private User user;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);

		user = new User();
		user.setId(1L);
		user.setUsername("venkat");
	}

	@Test
	void generateAuditReport_userNotFound() {

		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> auditService.generateAuditReport(1L));
	}

	@Test
	void generateAuditReportByUsername_userNotFound() {

		when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> auditService.generateAuditReportByUsername("venkat"));
	}

	@Test
	void generateAuditReport_success_withWeakReusedOld() {

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		PasswordEntry entry1 = new PasswordEntry();
		entry1.setAccountName("Google");
		entry1.setEncryptedPassword(AESUtil.encrypt("12345"));
		entry1.setUpdatedAt(LocalDateTime.now().minusDays(120));
		entry1.setUser(user);

		PasswordEntry entry2 = new PasswordEntry();
		entry2.setAccountName("Facebook");
		entry2.setEncryptedPassword(AESUtil.encrypt("12345"));
		entry2.setUpdatedAt(LocalDateTime.now());
		entry2.setUser(user);

		when(passwordEntryRepository.findByUser(user)).thenReturn(List.of(entry1, entry2));

		AuditReportDTO report = auditService.generateAuditReport(1L);

		assertEquals(2, report.getTotalPasswords());
		assertEquals(2, report.getWeakPasswords());
		assertEquals(2, report.getReusedPasswords());
		assertEquals(1, report.getOldPasswords());

		assertTrue(report.getWeakPasswordAccounts().contains("Google"));
		assertTrue(report.getReusedPasswordAccounts().contains("Facebook"));
		assertTrue(report.getOldPasswordAccounts().contains("Google"));
	}

	@Test
	void generateAuditReport_noIssues() {

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		PasswordEntry entry = new PasswordEntry();
		entry.setAccountName("SecureApp");
		entry.setEncryptedPassword(AESUtil.encrypt("Abc@12345XYZ!"));
		entry.setUpdatedAt(LocalDateTime.now());
		entry.setUser(user);

		when(passwordEntryRepository.findByUser(user)).thenReturn(List.of(entry));

		AuditReportDTO report = auditService.generateAuditReport(1L);

		assertEquals(1, report.getTotalPasswords());
		assertEquals(0, report.getWeakPasswords());
		assertEquals(0, report.getReusedPasswords());
		assertEquals(0, report.getOldPasswords());
	}

	@Test
	void generateAuditReportByUsername_success() {

		when(userRepository.findByUsername("venkat")).thenReturn(Optional.of(user));

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		when(passwordEntryRepository.findByUser(user)).thenReturn(List.of());

		AuditReportDTO report = auditService.generateAuditReportByUsername("venkat");

		assertEquals(0, report.getTotalPasswords());
	}
}