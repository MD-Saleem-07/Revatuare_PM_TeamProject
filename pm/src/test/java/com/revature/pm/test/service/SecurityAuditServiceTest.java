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

import com.revature.pm.dto.AuditReportDTO;
import com.revature.pm.entity.PasswordEntry;
import com.revature.pm.entity.User;
import com.revature.pm.exception.ResourceNotFoundException;
import com.revature.pm.repository.PasswordEntryRepository;
import com.revature.pm.repository.UserRepository;
import com.revature.pm.service.SecurityAuditService;
import com.revature.pm.util.AESUtil;
import com.revature.pm.util.PasswordGeneratorUtil;

@ExtendWith(MockitoExtension.class)
class SecurityAuditServiceTest {

	@InjectMocks
	private SecurityAuditService service;

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEntryRepository passwordEntryRepository;

	@Test
	void generateAuditReportByUsername_userNotFound() {
		when(userRepository.findByUsername("venkat")).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> service.generateAuditReportByUsername("venkat"));
	}

	@Test
	void generateAuditReport_userNotFound() {
		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> service.generateAuditReport(1L));
	}

	@Test
	void generateAuditReport_success_allScenarios() {

		User user = new User();
		user.setId(1L);
		user.setUsername("venkat");

		PasswordEntry e1 = new PasswordEntry();
		e1.setAccountName("gmail");
		e1.setEncryptedPassword("enc1");
		e1.setUpdatedAt(LocalDateTime.now().minusDays(100));
		e1.setUser(user);

		PasswordEntry e2 = new PasswordEntry();
		e2.setAccountName("facebook");
		e2.setEncryptedPassword("enc2");
		e2.setUpdatedAt(LocalDateTime.now());
		e2.setUser(user);

		PasswordEntry e3 = new PasswordEntry();
		e3.setAccountName("twitter");
		e3.setEncryptedPassword("enc2");
		e3.setUpdatedAt(LocalDateTime.now());
		e3.setUser(user);

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(passwordEntryRepository.findByUser(user)).thenReturn(List.of(e1, e2, e3));

		try (MockedStatic<AESUtil> aesMock = mockStatic(AESUtil.class);
				MockedStatic<PasswordGeneratorUtil> strengthMock = mockStatic(PasswordGeneratorUtil.class)) {

			aesMock.when(() -> AESUtil.decrypt("enc1")).thenReturn("weakPass");
			aesMock.when(() -> AESUtil.decrypt("enc2")).thenReturn("strongPass");

			strengthMock.when(() -> PasswordGeneratorUtil.checkStrength("weakPass")).thenReturn("Weak");

			strengthMock.when(() -> PasswordGeneratorUtil.checkStrength("strongPass")).thenReturn("Strong");

			AuditReportDTO report = service.generateAuditReport(1L);

			assertEquals(3, report.getTotalPasswords());

			assertEquals(1, report.getWeakPasswords());
			assertTrue(report.getWeakPasswordAccounts().contains("gmail"));

			assertEquals(2, report.getReusedPasswords());
			assertTrue(report.getReusedPasswordAccounts().contains("facebook"));
			assertTrue(report.getReusedPasswordAccounts().contains("twitter"));

			assertEquals(1, report.getOldPasswords());
			assertTrue(report.getOldPasswordAccounts().contains("gmail"));
		}
	}
}
