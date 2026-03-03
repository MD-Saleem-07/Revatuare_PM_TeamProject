package com.revature.pm.security;

import com.revature.pm.entity.User;
import com.revature.pm.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private UserRepository userRepository;

	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {

		User user;

		if (usernameOrEmail.contains("@")) {
			user = userRepository.findByEmail(usernameOrEmail)
					.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		} else {
			user = userRepository.findByUsername(usernameOrEmail)
					.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		}

		return new CustomUserDetails(user);
	}
}