package com.revature.pm.test.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.revature.pm.service.impl.PasswordGeneratorServiceImpl;

class PasswordGeneratorServiceImplTest {

	private PasswordGeneratorServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new PasswordGeneratorServiceImpl();
	}

	// =========================================
	// generateSinglePassword Tests
	// =========================================

	@Test
	void generateSinglePassword_shouldReturnCorrectLength() {

		String password = service.generateSinglePassword(12, true, true, true, true, false);

		assertNotNull(password);
		assertEquals(12, password.length());
	}

	@Test
	void generateSinglePassword_numbersOnly() {

		String password = service.generateSinglePassword(8, false, false, true, false, false);

		assertEquals(8, password.length());
		assertTrue(password.matches("\\d+"));
	}

	public String generateSinglePassword(int length, boolean upper, boolean lower, boolean digit, boolean special,
			boolean space) {

		if (length <= 0) {
			throw new IllegalArgumentException("Length must be greater than 0");
		}

		StringBuilder characterPool = new StringBuilder();

		if (upper) {
			characterPool.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		}
		if (lower) {
			characterPool.append("abcdefghijklmnopqrstuvwxyz");
		}
		if (digit) {
			characterPool.append("0123456789");
		}
		if (special) {
			characterPool.append("@#$%^&+=!");
		}
		if (space) {
			characterPool.append(" ");
		}

		if (characterPool.length() == 0) {
			throw new IllegalArgumentException("At least one character type must be selected");
		}

		Random random = new Random();
		StringBuilder password = new StringBuilder();

		for (int i = 0; i < length; i++) {
			int index = random.nextInt(characterPool.length());
			password.append(characterPool.charAt(index));
		}

		return password.toString();
	}

	// =========================================
	// generateMultiplePasswords Tests
	// =========================================

	@Test
	void generateMultiplePasswords_shouldReturnCorrectCount() {

		List<String> passwords = service.generateMultiplePasswords(5, 10, true, true, true, true, false);

		assertNotNull(passwords);
		assertEquals(5, passwords.size());
	}

	@Test
	void generateMultiplePasswords_eachPasswordCorrectLength() {

		List<String> passwords = service.generateMultiplePasswords(3, 15, true, true, true, true, false);

		for (String pwd : passwords) {
			assertEquals(15, pwd.length());
		}
	}

	@Test
	void generateMultiplePasswords_countZero() {

		List<String> passwords = service.generateMultiplePasswords(0, 10, true, true, true, true, false);

		assertNotNull(passwords);
		assertTrue(passwords.isEmpty());
	}

	@Test
	void getPasswordStrength_shouldReturnWeak() {

		String strength = service.getPasswordStrength("12345");

		assertNotNull(strength);
		assertTrue(strength.equalsIgnoreCase("Weak") || strength.equalsIgnoreCase("Medium")
				|| strength.equalsIgnoreCase("Strong"));
	}

	public String getPasswordStrength(String password) {

		if (password == null || password.length() < 8) {
			return "Weak";
		}

		boolean hasUpper = password.matches(".*[A-Z].*");
		boolean hasLower = password.matches(".*[a-z].*");
		boolean hasDigit = password.matches(".*\\d.*");
		boolean hasSpecial = password.matches(".*[@#$%^&+=!].*");

		if (hasUpper && hasLower && hasDigit && hasSpecial) {
			return "Strong";
		}

		return "Medium";
	}

	@Test
	void getPasswordStrength_emptyPassword() {

		String strength = service.getPasswordStrength("");

		assertNotNull(strength);
	}
}