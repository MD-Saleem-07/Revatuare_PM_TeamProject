package com.revature.pm;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class GlobalModelAttributes {

	@ModelAttribute
	public void addUsername(HttpSession session, Model model) {

		String username = (String) session.getAttribute("USERNAME");

		if (username != null) {
			model.addAttribute("username", username);
		}
	}
}