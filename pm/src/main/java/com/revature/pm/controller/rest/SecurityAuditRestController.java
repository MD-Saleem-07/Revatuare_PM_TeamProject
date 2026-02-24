package com.revature.pm.controller.rest;

import com.revature.pm.dto.AuditReportDTO;
import com.revature.pm.service.SecurityAuditService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit")
public class SecurityAuditRestController {

	@Autowired
	private SecurityAuditService securityAuditService;

	@GetMapping
	public ResponseEntity<AuditReportDTO> generateAuditReport(Authentication authentication) {

		String username = authentication.getName();

		AuditReportDTO report = securityAuditService.generateAuditReportByUsername(username);

		return ResponseEntity.ok(report);
	}
}
