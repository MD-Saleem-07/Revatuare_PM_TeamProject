package com.revature.pm.test.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.revature.pm.dto.*;
import com.revature.pm.entity.PasswordEntry;
import com.revature.pm.entity.User;
import com.revature.pm.exception.InvalidOperationException;
import com.revature.pm.repository.PasswordEntryRepository;
import com.revature.pm.repository.UserRepository;
import com.revature.pm.service.PasswordEntryService;
import com.revature.pm.util.AESUtil;

@ExtendWith(MockitoExtension.class)
class PasswordEntryServiceTest {

	@InjectMocks
	private PasswordEntryService service;

	@Mock
	private PasswordEntryRepository passwordEntryRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private BCryptPasswordEncoder passwordEncoder;

	private User user;
	private PasswordEntry entry;

	@BeforeEach
	void setup() {
		user = new User();
		user.setId(1L);
		user.setUsername("saleem");
		user.setMasterPassword("encoded");

		entry = new PasswordEntry();
		entry.setId(100L);
		entry.setAccountName("gmail");
		entry.setLoginUsername("saleem@gmail.com");
		entry.setEncryptedPassword(AESUtil.encrypt("Password@123"));
		entry.setUser(user);
		entry.setCreatedAt(LocalDateTime.now());
		entry.setUpdatedAt(LocalDateTime.now());
	}

	// ================= ADD =================

	@Test
	void addPassword_success() {
		when(userRepository.findByUsername("saleem")).thenReturn(Optional.of(user));

		PasswordEntryDTO dto = new PasswordEntryDTO();
		dto.setAccountName("gmail");
		dto.setPassword("pass");
		dto.setLoginUsername("login");

		service.addPasswordByUsername("saleem", dto);

		verify(passwordEntryRepository).save(any(PasswordEntry.class));
	}

	// ================= GET ALL =================

	@Test
	void getAllPasswords_success() {
		when(userRepository.findByUsername("saleem")).thenReturn(Optional.of(user));
		when(passwordEntryRepository.findByUser(user)).thenReturn(List.of(entry));

		List<PasswordEntryDTO> result = service.getAllPasswordsByUsername("saleem");

		assertEquals(1, result.size());
		assertEquals("gmail", result.get(0).getAccountName());
	}

	// ================= UPDATE =================

	@Test
	void updatePassword_success() {
		when(userRepository.findByUsername("saleem")).thenReturn(Optional.of(user));
		when(passwordEntryRepository.findById(100L)).thenReturn(Optional.of(entry));

		PasswordEntryDTO dto = new PasswordEntryDTO();
		dto.setAccountName("updated");
		dto.setPassword("newPass");

		service.updatePasswordByUsername("saleem", 100L, dto);

		verify(passwordEntryRepository).save(entry);
		assertEquals("updated", entry.getAccountName());
	}

	// ================= DELETE =================

	@Test
	void deletePassword_success() {
		when(userRepository.findByUsername("saleem")).thenReturn(Optional.of(user));
		when(passwordEntryRepository.findById(100L)).thenReturn(Optional.of(entry));

		service.deletePasswordByUsername("saleem", 100L);

		verify(passwordEntryRepository).delete(entry);
	}

	// ================= TOGGLE FAVORITE =================

	@Test
	void toggleFavorite_success() {
		when(userRepository.findByUsername("saleem")).thenReturn(Optional.of(user));
		when(passwordEntryRepository.findById(100L)).thenReturn(Optional.of(entry));

		service.toggleFavoriteByUsername("saleem", 100L);

		assertTrue(entry.isFavorite());
		verify(passwordEntryRepository).save(entry);
	}

	// ================= VIEW PASSWORD =================

	@Test
	void viewPassword_success() {
		when(userRepository.findByUsername("saleem")).thenReturn(Optional.of(user));
		when(passwordEntryRepository.findById(100L)).thenReturn(Optional.of(entry));
		when(passwordEncoder.matches("master", "encoded")).thenReturn(true);

		ViewPasswordDTO dto = new ViewPasswordDTO();
		dto.setMasterPassword("master");

		String result = service.viewPasswordByUsername("saleem", 100L, dto);

		assertEquals("Password@123", result);
	}

	@Test
	void viewPassword_wrongMasterPassword() {
		when(userRepository.findByUsername("saleem")).thenReturn(Optional.of(user));
		when(passwordEntryRepository.findById(100L)).thenReturn(Optional.of(entry));
		when(passwordEncoder.matches(any(), any())).thenReturn(false);

		ViewPasswordDTO dto = new ViewPasswordDTO();
		dto.setMasterPassword("wrong");

		assertThrows(InvalidOperationException.class, () -> service.viewPasswordByUsername("saleem", 100L, dto));
	}

	// ================= SEARCH =================

	@Test
	void searchPasswords_success() {
		when(userRepository.findByUsername("saleem")).thenReturn(Optional.of(user));
		when(passwordEntryRepository.findByUserAndAccountNameContainingIgnoreCase(user, "gm"))
				.thenReturn(List.of(entry));

		List<PasswordEntryDTO> result = service.searchPasswordsByUsername("saleem", "gm");

		assertEquals(1, result.size());
	}

	// ================= FILTER =================

	@Test
	void filterByCategory_success() {
		when(userRepository.findByUsername("saleem")).thenReturn(Optional.of(user));
		when(passwordEntryRepository.findByUserAndCategoryIgnoreCase(user, "Social")).thenReturn(List.of(entry));

		List<PasswordEntryDTO> result = service.filterByCategoryByUsername("saleem", "Social");

		assertEquals(1, result.size());
	}

	// ================= SORT =================

	@Test
	void sortPasswords_success() {
		when(userRepository.findByUsername("saleem")).thenReturn(Optional.of(user));
		when(passwordEntryRepository.findByUser(eq(user), any(Sort.class))).thenReturn(List.of(entry));

		List<PasswordEntryDTO> result = service.sortPasswordsByUsername("saleem", "name");

		assertEquals(1, result.size());
	}

	// ================= DASHBOARD =================

	@Test
	void dashboardStats_success() {
		when(userRepository.findByUsername("saleem")).thenReturn(Optional.of(user));
		when(passwordEntryRepository.findByUser(user)).thenReturn(List.of(entry));

		DashboardStatsDTO stats = service.getDashboardStatsByUsername("saleem");

		assertEquals(1, stats.getTotalPasswords());
	}

	// ================= PAGINATION =================

	@Test
	void getPasswordsPage_success() {
		when(userRepository.findByUsername("saleem")).thenReturn(Optional.of(user));

		Page<PasswordEntry> page = new PageImpl<>(List.of(entry));

		when(passwordEntryRepository.findByUser(eq(user), any(Pageable.class))).thenReturn(page);

		Page<PasswordEntryDTO> result = service.getPasswordsPageByUsername("saleem", 0, 5);

		assertEquals(1, result.getTotalElements());
	}
}