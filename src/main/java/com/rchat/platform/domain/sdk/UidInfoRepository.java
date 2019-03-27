package com.rchat.platform.domain.sdk;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.rchat.platform.domain.TalkbackGroup;

public interface UidInfoRepository extends JpaRepository<TalkbackGroup, String>{
	@Query("select u from UidInfo u where u.uid=?1")
	List<UidInfo> getbByUid(String uid);
	

}
