package com.revature.pm.service;

import java.util.List;

public interface PasswordGeneratorService {

	String generateSinglePassword(int length, boolean upper, boolean lower, boolean numbers, boolean special,
			boolean excludeSimilar);

	List<String> generateMultiplePasswords(int count, int length, boolean upper, boolean lower, boolean numbers,
			boolean special, boolean excludeSimilar);

	String getPasswordStrength(String password);
}