package com.rchat.platform.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public abstract class GroupFileRepositoryImpl implements GroupFileRepository {

    @PersistenceContext
    private EntityManager em;
    
    @Override
    public List<GroupFile> findByFileId(String fileId) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<GroupFile> groupFileQuery = builder.createQuery(GroupFile.class);
        Root<GroupFile> groupFile = groupFileQuery.from(GroupFile.class);
        List<GroupFile> result = new ArrayList<>();
        if(null != fileId){       	
        	Predicate p;        	
        	p = builder.equal(groupFile.get(GroupFile_.fileId), fileId); 
        	groupFileQuery.where(p);
        	TypedQuery<GroupFile> typedQuery = em.createQuery(groupFileQuery);
        	result = typedQuery.getResultList();
        }
        return result;
    }
    
    @Override
    public List<GroupFile> findByGroupAndType(Group group, FileType type) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<GroupFile> groupFileQuery = builder.createQuery(GroupFile.class);
        Root<GroupFile> groupFile = groupFileQuery.from(GroupFile.class);
        List<GroupFile> result = new ArrayList<>();
        if(null != group){       	
        	Predicate p;        	
        	p = builder.equal(groupFile.get(GroupFile_.group), group); 
            if(null != type){       	       	
            	p = builder.equal(groupFile.get(GroupFile_.type), type); 
            }
            groupFileQuery.where(p);
            TypedQuery<GroupFile> typedQuery = em.createQuery(groupFileQuery);
            result = typedQuery.getResultList();
        }
        return result;
    }

}
