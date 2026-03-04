package com.revature.pm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SecurityQuestionDTO {

	private Long id;
	@NotBlank(message = "Security question is required")
	@Size(max = 255, message = "Security question too long")
	private String question;

	@NotBlank(message = "Security answer is required")
	@Size(min = 3, max = 255, message = "Answer must be between 2 and 255 characters")
	private String answer;

	public SecurityQuestionDTO() {
	}

	public SecurityQuestionDTO(Long id, String question, String answer) {
		this.id = id;
		this.question = question;
		this.answer = answer;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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