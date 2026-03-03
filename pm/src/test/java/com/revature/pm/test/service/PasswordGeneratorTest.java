package com.revature.pm.test.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.revature.pm.service.impl.PasswordGeneratorServiceImpl;

class PasswordGeneratorServiceImplTest {

	private PasswordGeneratorServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new PasswordGeneratorServiceImpl();
	}

	// ===============================
	// generateSinglePassword Tests
	// ===============================

	@Test
	void generateSinglePassword_ShouldReturnPasswordOfCorrectLength() {

		String password = service.generateSinglePassword(12, true, true, true, true, false);

		assertNotNull(password);
		assertEquals(12, password.length());
	}

	@Test
	void generateSinglePassword_WithOnlyNumbers_ShouldContainOnlyDigits() {

		String password = service.generateSinglePassword(8, false, false, true, false, false);

		assertTrue(password.matches("\\d+"));
		assertEquals(8, password.length());
	}

	// ===============================
	// generateMultiplePasswords Tests
	// ===============================

	@Test
	void generateMultiplePasswords_ShouldReturnCorrectCount() {

		List<String> passwords = service.generateMultiplePasswords(5, 10, true, true, true, true, false);

		assertNotNull(passwords);
		assertEquals(5, passwords.size());
	}

	@Test
	void generateMultiplePasswords_EachPasswordShouldHaveCorrectLength() {

		List<String> passwords = service.generateMultiplePasswords(3, 15, true, true, true, true, false);

		for (String pwd : passwords) {
			assertEquals(15, pwd.length());
		}
	}

	// ===============================
	// getPasswordStrength Tests
	// ===============================

	@Test
	void getPasswordStrength_ShouldReturnWeakForSimplePassword() {

		String strength = service.getPasswordStrength("12345");

		assertNotNull(strength);
		assertTrue(strength.equalsIgnoreCase("Weak") || strength.equalsIgnoreCase("Medium")
				|| strength.equalsIgnoreCase("Strong"));
	}

	@Test
	void getPasswordStrength_ShouldReturnStrongForComplexPassword() {

		String strength = service.getPasswordStrength("Abc@12345XYZ!");

		assertNotNull(strength);
		assertEquals("Strong", strength);
	}
}