package com.rchat.platform.domain;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	Optional<User> findByUsername(String username);

	@Modifying
	@Query("update User u set u.password = ?2, u.salt = ?3 where u.id = ?1")
	int updatePassword(String id, String newPassword, String salt);

	@Modifying
	@Query("update User u set u.online = ?2, u.updatedAt = ?3 where u.id = ?1")
	int setOnline(String id, boolean online, Date updatedAt);
}
