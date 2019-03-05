package com.rchat.platform.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TalkbackUserRepository extends JpaRepository<TalkbackUser, String>, TalkbackUserRepositoryCustom {

    Page<TalkbackUser> findByGroup(Group group, Pageable pageable);

    Page<TalkbackUser> findByDepartmentIn(List<Department> departments, Pageable pageable);

    /**
     * 回收账号
     *
     * @param id 对讲账号id
     * @return 更新数目
     */
    @Modifying
    @Query("update TalkbackUser u set u.activated = false, u.department = null where u.id = ?1")
    int recycle(String id);

    /**
     * 停用账号
     *
     * @param id      对讲账号id
     * @param enabled 是能与否
     * @return 更新数目
     */
    @Modifying
    @Query("update TalkbackUser u set u.enabled = ?2 where u.id = ?1")
    int setEnabled(String id, boolean enabled);

    Page<TalkbackUser> findByGroups(TalkbackGroup group, Pageable pageable);

    List<TalkbackUser> findByIdIn(List<String> userIds);

    long countByGroupsIn(List<TalkbackGroup> group);

    List<TalkbackUser> findByNumberGroupSegment(GroupSegment segment);

    boolean existsByNumberGroupSegment(GroupSegment segment);

    long countByGroup(Group group);

    long countByGroupServer(Server server);
    @Modifying
    @Query("update TalkbackUser u set u.talkbackGroupId = ?2 where u.id = ?1")
	void updateDefaultGruops(String string, String talkBackGroupsId);
    @Query("select t from TalkbackUser t where t.userOnlyId=?1")
    List<TalkbackUser> findNum(int hashCode);

}
