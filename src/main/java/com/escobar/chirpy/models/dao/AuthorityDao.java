package com.escobar.chirpy.models.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.escobar.chirpy.models.entity.Authority;

@Repository
public class AuthorityDao {

	@PersistenceContext
	private EntityManager em;
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<Authority> findAll(){
	    Query query = em.createQuery("SELECT a FROM Authority a");
	    return query.getResultList();
	}
	
	public Authority findByName(String nameAuth) {
            TypedQuery<Authority> query = em.createQuery("SELECT a FROM Authority a WHERE a.authority = :authority", Authority.class); 
	    query.setParameter("authority", nameAuth);
	    return query.getSingleResult();
	}
        
        public void save(Authority au){
            em.persist(au);
        }
	
	
}
