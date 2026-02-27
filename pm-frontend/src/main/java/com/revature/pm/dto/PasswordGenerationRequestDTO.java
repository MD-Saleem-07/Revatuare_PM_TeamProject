package com.revature.pm.dto;

public class PasswordGenerationRequestDTO {

	private int length;
	private boolean includeUpper;
	private boolean includeLower;
	private boolean includeNumbers;
	private boolean includeSpecial;
	private boolean excludeSimilar;

	public PasswordGenerationRequestDTO() {
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public boolean isIncludeUpper() {
		return includeUpper;
	}

	public void setIncludeUpper(boolean includeUpper) {
		this.includeUpper = includeUpper;
	}

	public boolean isIncludeLower() {
		return includeLower;
	}

	public void setIncludeLower(boolean includeLower) {
		this.includeLower = includeLower;
	}

	public boolean isIncludeNumbers() {
		return includeNumbers;
	}

	public void setIncludeNumbers(boolean includeNumbers) {
		this.includeNumbers = includeNumbers;
	}

	public boolean isIncludeSpecial() {
		return includeSpecial;
	}

	public void setIncludeSpecial(boolean includeSpecial) {
		this.includeSpecial = includeSpecial;
	}

	public boolean isExcludeSimilar() {
		return excludeSimilar;
	}

	public void setExcludeSimilar(boolean excludeSimilar) {
		this.excludeSimilar = excludeSimilar;
	}

}
