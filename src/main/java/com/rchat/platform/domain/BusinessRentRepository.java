package com.rchat.platform.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessRentRepository extends JpaRepository<BusinessRent, String> {
	public List<BusinessRent> findByUser(TalkbackUser user);
}
