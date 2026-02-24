package com.revature.pm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revature.pm.entity.SecurityQuestion;
import com.revature.pm.entity.User;

@Repository
public interface SecurityQuestionRepository extends JpaRepository<SecurityQuestion, Long> {

	List<SecurityQuestion> findByUser(User user);
}