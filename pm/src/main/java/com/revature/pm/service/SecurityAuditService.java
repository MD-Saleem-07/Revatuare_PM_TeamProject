package com.revature.pm.service;

import com.revature.pm.dto.AuditReportDTO;

public interface SecurityAuditService {

    AuditReportDTO generateAuditReportByUsername(String username);

    AuditReportDTO generateAuditReport(Long userId);
}
