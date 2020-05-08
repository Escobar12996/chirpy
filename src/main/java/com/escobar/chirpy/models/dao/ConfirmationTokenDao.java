package com.escobar.chirpy.models.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.escobar.chirpy.models.entity.ConfirmationToken;

@Repository
public class ConfirmationTokenDao {

	@PersistenceContext
	private EntityManager em;
        
	@Transactional(readOnly = true)
	public ConfirmationToken findConfirmationToken(String token) {
		TypedQuery<ConfirmationToken> query = em.createQuery("SELECT c FROM ConfirmationToken c WHERE c.confirmationToken = :token", ConfirmationToken.class); 
        query.setParameter("token", token);
        
        try {
        	return query.getSingleResult();
        } catch (Exception e) {
			return null;
		}
	}
	
	@Transactional
	public void save(ConfirmationToken confirm) {
		em.persist(confirm);	
	}
	
	@Transactional
	public void update(ConfirmationToken confirm) {
		em.merge(confirm);	
	}
	
	@Transactional
	public void remove(ConfirmationToken confirm) {
		em.remove(confirm);
	}
}
