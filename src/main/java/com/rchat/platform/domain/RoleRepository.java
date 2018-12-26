package com.rchat.platform.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String>, RoleRepositoryCustom {
	@Query(value = "select r.* from t_role r inner join t_user_role ur on r.id=ur.role_id where ur.user_id=?1", nativeQuery = true)
	List<Role> findByUser(String userId);
}
