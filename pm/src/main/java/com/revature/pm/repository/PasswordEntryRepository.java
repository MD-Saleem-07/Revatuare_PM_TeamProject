package com.revature.pm.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revature.pm.entity.PasswordEntry;
import com.revature.pm.entity.User;

@Repository
public interface PasswordEntryRepository extends JpaRepository<PasswordEntry, Long> {

	List<PasswordEntry> findByUser(User user);

	Page<PasswordEntry> findByUser(User user, Pageable pageable);// added newly

	List<PasswordEntry> findByUser(User user, Sort sort);

	List<PasswordEntry> findByUserAndFavoriteTrue(User user);

	List<PasswordEntry> findByUserAndCategoryIgnoreCase(User user, String category);

	List<PasswordEntry> findByUserAndAccountNameContainingIgnoreCase(User user, String keyword);

	List<PasswordEntry> findByUserAndLoginUsernameContainingIgnoreCase(User user, String keyword);

	List<PasswordEntry> findByUserAndWebsiteUrlContainingIgnoreCase(User user, String keyword);
}