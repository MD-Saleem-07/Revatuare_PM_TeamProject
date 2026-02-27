package com.revature.pm.controller.rest;

import com.revature.pm.dto.AuditReportDTO;
import com.revature.pm.service.SecurityAuditService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit")
public class SecurityAuditRestController {

	private SecurityAuditService securityAuditService;

	public SecurityAuditRestController(SecurityAuditService securityAuditService) {
		this.securityAuditService = securityAuditService;
	}

	@GetMapping
	public ResponseEntity<AuditReportDTO> generateAuditReport(Authentication authentication) {

		String username = authentication.getName();

		AuditReportDTO report = securityAuditService.generateAuditReportByUsername(username);

		return ResponseEntity.ok(report);
	}
}