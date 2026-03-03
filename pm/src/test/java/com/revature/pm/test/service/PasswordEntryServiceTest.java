package com.revature.pm.test.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.revature.pm.dto.*;
import com.revature.pm.entity.*;
import com.revature.pm.exception.*;
import com.revature.pm.repository.*;
import com.revature.pm.service.impl.PasswordEntryServiceImpl;

class PasswordEntryServiceImplTest {

	@Mock
	private PasswordEntryRepository passwordEntryRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private BCryptPasswordEncoder passwordEncoder;

	@InjectMocks
	private PasswordEntryServiceImpl service;

	private User user;
	private PasswordEntry entry;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);

		user = new User();
		user.setId(1L);
		user.setUsername("saleem");
		user.setMasterPassword("encoded");

		entry = new PasswordEntry();
		entry.setId(100L);
		entry.setUser(user);
		entry.setAccountName("Google");
		entry.setEncryptedPassword("encrypted");
		entry.setCreatedAt(LocalDateTime.now());
		entry.setUpdatedAt(LocalDateTime.now());
	}

	@Test
	void addPassword_success() {

		PasswordEntryDTO dto = new PasswordEntryDTO();
		dto.setAccountName("Google");
		dto.setPassword("abc123");

		when(userRepository.findByUsername("saleem")).thenReturn(Optional.of(user));

		service.addPasswordByUsername("saleem", dto);

		verify(passwordEntryRepository).save(any());
	}

	@Test
	void addPassword_userNotFound() {

		when(userRepository.findByUsername("saleem")).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class,
				() -> service.addPasswordByUsername("saleem", new PasswordEntryDTO()));
	}

	@Test
	void getAllPasswords_success() {

		when(userRepository.findByUsername("saleem")).thenReturn(Optional.of(user));

		when(passwordEntryRepository.findByUser(user)).thenReturn(List.of(entry));

		List<PasswordEntryDTO> result = service.getAllPasswordsByUsername("saleem");

		assertEquals(1, result.size());
		assertEquals("Google", result.get(0).getAccountName());
	}

	@Test
	void updatePassword_success() {

		PasswordEntryDTO dto = new PasswordEntryDTO();
		dto.setAccountName("Updated");
		dto.setPassword("newPass");

		when(userRepository.findByUsername("saleem")).thenReturn(Optional.of(user));

		when(passwordEntryRepository.findById(100L)).thenReturn(Optional.of(entry));

		service.updatePasswordByUsername("saleem", 100L, dto);

		verify(passwordEntryRepository).save(entry);
	}

	@Test
	void updatePassword_unauthorized() {

		User anotherUser = new User();
		anotherUser.setId(2L);

		entry.setUser(anotherUser);

		when(userRepository.findByUsername("saleem")).thenReturn(Optional.of(user));

		when(passwordEntryRepository.findById(100L)).thenReturn(Optional.of(entry));

		assertThrows(RuntimeException.class,
				() -> service.updatePasswordByUsername("saleem", 100L, new PasswordEntryDTO()));
	}

	@Test
	void deletePassword_success() {

		when(userRepository.findByUsername("saleem")).thenReturn(Optional.of(user));

		when(passwordEntryRepository.findById(100L)).thenReturn(Optional.of(entry));

		service.deletePasswordByUsername("saleem", 100L);

		verify(passwordEntryRepository).delete(entry);
	}

	@Test
	void toggleFavorite_success() {

		entry.setFavorite(false);

		when(userRepository.findByUsername("saleem")).thenReturn(Optional.of(user));

		when(passwordEntryRepository.findById(100L)).thenReturn(Optional.of(entry));

		service.toggleFavoriteByUsername("saleem", 100L);

		assertTrue(entry.isFavorite());
		verify(passwordEntryRepository).save(entry);
	}

	@Test
	void viewPassword_wrongMasterPassword() {

		ViewPasswordDTO dto = new ViewPasswordDTO();
		dto.setMasterPassword("wrong");

		when(userRepository.findByUsername("saleem")).thenReturn(Optional.of(user));

		when(passwordEntryRepository.findById(100L)).thenReturn(Optional.of(entry));

		when(passwordEncoder.matches(any(), any())).thenReturn(false);

		assertThrows(InvalidOperationException.class, () -> service.viewPasswordByUsername("saleem", 100L, dto));
	}

	@Test
	void getPasswordsPage_success() {

		when(userRepository.findByUsername("saleem")).thenReturn(Optional.of(user));

		Page<PasswordEntry> page = new PageImpl<>(List.of(entry));

		when(passwordEntryRepository.findByUser(eq(user), any(Pageable.class))).thenReturn(page);

		Page<PasswordEntryDTO> result = service.getPasswordsPageByUsername("saleem", 0, 5);

		assertEquals(1, result.getContent().size());
	}
}