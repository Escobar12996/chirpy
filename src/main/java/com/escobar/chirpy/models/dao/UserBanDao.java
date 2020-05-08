package com.escobar.chirpy.models.dao;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.escobar.chirpy.models.entity.User;
import com.escobar.chirpy.models.entity.UserBan;

@Repository
public class UserBanDao {

	@PersistenceContext
	private EntityManager em;
    
	@Transactional(readOnly = true)
	public List<UserBan> findByUserBan(User user) {
		TypedQuery<UserBan> query = em.createQuery("SELECT u FROM UserBan u WHERE u.userReceivedBan = :id", UserBan.class); 
        query.setParameter("id", user);
        
        return query.getResultList();
	}
	
	
	
	@Transactional
	public void save(UserBan usb) {
		em.persist(usb);	
	}
}
