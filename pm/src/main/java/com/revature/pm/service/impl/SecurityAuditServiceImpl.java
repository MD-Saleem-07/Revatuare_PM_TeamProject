package com.revature.pm.service.impl;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.revature.pm.dto.AuditReportDTO;
import com.revature.pm.entity.PasswordEntry;
import com.revature.pm.entity.User;
import com.revature.pm.exception.ResourceNotFoundException;
import com.revature.pm.repository.PasswordEntryRepository;
import com.revature.pm.repository.UserRepository;
import com.revature.pm.service.SecurityAuditService;
import com.revature.pm.util.AESUtil;
import com.revature.pm.util.PasswordGeneratorUtil;

@Service
public class SecurityAuditServiceImpl implements SecurityAuditService {

    private final UserRepository userRepository;
    private final PasswordEntryRepository passwordEntryRepository;

    private static final Logger logger =
            LoggerFactory.getLogger(SecurityAuditServiceImpl.class);

    public SecurityAuditServiceImpl(UserRepository userRepository,
                                    PasswordEntryRepository passwordEntryRepository) {
        this.userRepository = userRepository;
        this.passwordEntryRepository = passwordEntryRepository;
    }
    

    @Override
    public AuditReportDTO generateAuditReportByUsername(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return generateAuditReport(user.getId());
    }

    @Override
    public AuditReportDTO generateAuditReport(Long userId) {

        logger.info("Generating security audit report for user {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Audit failed - User not found: {}", userId);
                    return new ResourceNotFoundException("User not found");
                });

        List<PasswordEntry> entries = passwordEntryRepository.findByUser(user);

        logger.debug("Total password entries found for audit (user {}): {}",
                userId, entries.size());

        AuditReportDTO report = new AuditReportDTO();
        report.setTotalPasswords(entries.size());

		List<String> weakAccounts = new ArrayList<>();
        List<String> reusedAccounts = new ArrayList<>();
        List<String> oldAccounts = new ArrayList<>();

        Map<String, List<PasswordEntry>> passwordMap = new HashMap<>();

        for (PasswordEntry entry : entries) {

            String decrypted = AESUtil.decrypt(entry.getEncryptedPassword());

            String strength = PasswordGeneratorUtil.checkStrength(decrypted);

            if ("Weak".equals(strength)) {
                weakAccounts.add(entry.getAccountName());
            }

            passwordMap.computeIfAbsent(decrypted,
                    k -> new ArrayList<>()).add(entry);

            if (entry.getUpdatedAt()
                    .isBefore(LocalDateTime.now().minusDays(90))) {

                oldAccounts.add(entry.getAccountName());
            }
        }

        for (List<PasswordEntry> group : passwordMap.values()) {
            if (group.size() > 1) {
                reusedAccounts.addAll(
                        group.stream()
                                .map(PasswordEntry::getAccountName)
                                .collect(Collectors.toList())
                );
            }
        }

        report.setWeakPasswords(weakAccounts.size());
        report.setReusedPasswords(reusedAccounts.size());
        report.setOldPasswords(oldAccounts.size());

        report.setWeakPasswordAccounts(weakAccounts);
        report.setReusedPasswordAccounts(reusedAccounts);
        report.setOldPasswordAccounts(oldAccounts);

        logger.info("Audit completed for user {} -> Total: {}, Weak: {}, Reused: {}, Old: {}",
                userId,
                report.getTotalPasswords(),
                report.getWeakPasswords(),
                report.getReusedPasswords(),
                report.getOldPasswords());

        return report;
    }
}