package com.revature.pm.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.revature.pm.service.PasswordGeneratorService;
import com.revature.pm.util.PasswordGeneratorUtil;

import java.util.ArrayList;
import java.util.List;

@Service
public class PasswordGeneratorServiceImpl implements PasswordGeneratorService {

	private static final Logger logger = LoggerFactory.getLogger(PasswordGeneratorServiceImpl.class);

	@Override
	public String generateSinglePassword(int length, boolean upper, boolean lower, boolean numbers, boolean special,
			boolean excludeSimilar) {

		logger.info("Generating single password with length: {}, options -> U:{} L:{} N:{} S:{} ExcludeSimilar:{}",
				length, upper, lower, numbers, special, excludeSimilar);

		String password = PasswordGeneratorUtil.generatePassword(length, upper, lower, numbers, special,
				excludeSimilar);

		logger.info("Password generated successfully (value not logged)");

		return password;
	}

	@Override
	public List<String> generateMultiplePasswords(int count, int length, boolean upper, boolean lower, boolean numbers,
			boolean special, boolean excludeSimilar) {

		logger.info("Generating {} passwords with length: {}, options -> U:{} L:{} N:{} S:{} ExcludeSimilar:{}", count,
				length, upper, lower, numbers, special, excludeSimilar);

		List<String> passwords = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			passwords.add(generateSinglePassword(length, upper, lower, numbers, special, excludeSimilar));
		}

		logger.info("Multiple password generation completed. Total generated: {}", passwords.size());

		return passwords;
	}

	@Override
	public String getPasswordStrength(String password) {

		logger.info("Checking password strength (password value not logged)");

		String strength = PasswordGeneratorUtil.checkStrength(password);

		logger.debug("Password strength evaluated as: {}", strength);

		return strength;
	}
}