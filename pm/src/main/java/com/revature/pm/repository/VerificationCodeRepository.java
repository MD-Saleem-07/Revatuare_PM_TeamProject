package com.revature.pm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revature.pm.entity.VerificationCode;
import com.revature.pm.entity.User;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

	Optional<VerificationCode> findByUserAndCode(User user, String code);

	Optional<VerificationCode> findTopByUserOrderByExpiryTimeDesc(User user);
}