package com.revature.pm.dto;

import java.util.List;

public class AuditReportDTO {

    private int totalPasswords;
    private int weakPasswords;
    private int reusedPasswords;
    private int oldPasswords;

    private List<String> weakPasswordAccounts;
    private List<String> reusedPasswordAccounts;
    private List<String> oldPasswordAccounts;

    public AuditReportDTO() {
    }

	public int getTotalPasswords() {
		return totalPasswords;
	}
	

	public void setTotalPasswords(int totalPasswords) {
		this.totalPasswords = totalPasswords;
	}

	public int getWeakPasswords() {
		return weakPasswords;
	}

	public void setWeakPasswords(int weakPasswords) {
		this.weakPasswords = weakPasswords;
	}

	public int getReusedPasswords() {
		return reusedPasswords;
	}

	public void setReusedPasswords(int reusedPasswords) {
		this.reusedPasswords = reusedPasswords;
	}

	public int getOldPasswords() {
		return oldPasswords;
	}

	public void setOldPasswords(int oldPasswords) {
		this.oldPasswords = oldPasswords;
	}

	public List<String> getWeakPasswordAccounts() {
		return weakPasswordAccounts;
	}

	public void setWeakPasswordAccounts(List<String> weakPasswordAccounts) {
		this.weakPasswordAccounts = weakPasswordAccounts;
	}

	public List<String> getReusedPasswordAccounts() {
		return reusedPasswordAccounts;
	}

	public void setReusedPasswordAccounts(List<String> reusedPasswordAccounts) {
		this.reusedPasswordAccounts = reusedPasswordAccounts;
	}

	public List<String> getOldPasswordAccounts() {
		return oldPasswordAccounts;
	}

	public void setOldPasswordAccounts(List<String> oldPasswordAccounts) {
		this.oldPasswordAccounts = oldPasswordAccounts;
	}

    
}