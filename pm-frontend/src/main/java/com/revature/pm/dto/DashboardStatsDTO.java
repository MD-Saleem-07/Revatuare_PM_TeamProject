package com.revature.pm.dto;

public class DashboardStatsDTO {

	private int totalPasswords;
	private int strongPasswords;
	private int weakPasswords;
	private int reusedPasswords;

	public DashboardStatsDTO() {
	}

	public int getTotalPasswords() {
		return totalPasswords;
	}

	public void setTotalPasswords(int totalPasswords) {
		this.totalPasswords = totalPasswords;
	}

	public int getStrongPasswords() {
		return strongPasswords;
	}

	public void setStrongPasswords(int strongPasswords) {
		this.strongPasswords = strongPasswords;
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
}
