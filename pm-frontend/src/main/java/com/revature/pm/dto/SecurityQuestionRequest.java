package com.revature.pm.dto;

import jakarta.validation.constraints.NotBlank;

public class SecurityQuestionRequest {

	@NotBlank(message = "Security question is required")
	private String question;

	@NotBlank(message = "Answer is required")
	private String answer;

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

}
