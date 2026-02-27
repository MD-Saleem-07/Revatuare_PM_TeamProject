package com.revature.pm.mapper;

import org.springframework.stereotype.Component;
import com.revature.pm.entity.SecurityQuestion;
import com.revature.pm.dto.SecurityQuestionDTO;

@Component
public class SecurityQuestionMapper {

	public SecurityQuestionDTO toDTO(SecurityQuestion entity) {
		if (entity == null)
			return null;

		return new SecurityQuestionDTO(entity.getId(), entity.getQuestion(), null // NEVER expose encrypted answer
		);
	}

	public SecurityQuestion toEntity(SecurityQuestionDTO dto) {
		if (dto == null)
			return null;

		SecurityQuestion question = new SecurityQuestion();
		question.setQuestion(dto.getQuestion());
		// encryptedAnswer will be added or set in service
		return question;
	}
}