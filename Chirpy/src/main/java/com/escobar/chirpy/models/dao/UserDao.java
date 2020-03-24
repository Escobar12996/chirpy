package com.escobar.chirpy.models.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.escobar.chirpy.models.entity.User;

@Repository
public class UserDao {

	@PersistenceContext
	private EntityManager em;
	
	@Autowired     
    private AuthorityDao authorityDao; 
	
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<User> findAll(){
	    Query query = em.createQuery("SELECT u FROM User u");
	    return query.getResultList();
	}
	
	public User findByUserName(String username) {
		TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class); 
	    query.setParameter("username", username);
            
            try {
                return query.getResultList().get(0);
            } catch (Exception e) {
                return null;
            }
	    
	}
        
        public List<User> findUsers(String username) {
		TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username LIKE CONCAT('%',:username,'%')", User.class); 
	    query.setParameter("username", username);
            
            return query.getResultList();
	}

	@Transactional
	public void save(User user) {
		em.persist(user);	
	}
	
	
}
