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
	
    public User findById(Long id) {
	TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.id = :id", User.class); 
    query.setParameter("id", id);
        
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
	}
        
	public User findByUserName(String username) {
		TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class); 
	    query.setParameter("username", username);
            
            try {
                return query.getSingleResult();
            } catch (Exception e) {
                return null;
            }
	}
        
        public User findEmail(String email) {
		TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class); 
	    query.setParameter("email", email);
            
            try {
                return query.getSingleResult();
            } catch (Exception e) {
                return null;
            }
        }
        
        public List<User> findUsersOnlyUsernameFirst(String username) {
		TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username LIKE CONCAT('%',:username,'%') order by u.id asc", User.class); 
	    query.setParameter("username", username);
            
            return query.setMaxResults(5).getResultList();
	}
        
        public List<User> findUsersOnlyUsernameNext(String username, Long id) {
		TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.id > :id and u.username LIKE CONCAT('%',:username,'%') order by u.id asc", User.class); 
	    query.setParameter("username", username);
            query.setParameter("id", id);

            return query.setMaxResults(5).getResultList();
	}

        public List<User> findUsersNameUsernameFirst(String username) {
		TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username LIKE CONCAT('%',:username,'%') or u.name LIKE CONCAT('%',:username,'%') order by u.id asc", User.class); 
	    query.setParameter("username", username);
            
            return query.setMaxResults(5).getResultList();
	}
        
        public List<User> findUsersNameUsernameNext(String username, Long id) {
		TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.id > :id and u.username LIKE CONCAT('%',:username,'%') or u.name LIKE CONCAT('%',:username,'%') order by u.id asc", User.class); 
	    query.setParameter("username", username);
            
            return query.getResultList();
	}
        
    public int userCount() {
		TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class); 
		return query.getResultList().size();
    }
        
    public List<User> findUsers(Long position, Long numbers){
    	TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.id > :idActual order by u.id", User.class); 
	    query.setParameter("idActual", (position*numbers)-numbers);
    	return query.setMaxResults(10).getResultList();
    }
        
        
        
        
        
        
        
	@Transactional
	public void save(User user) {
		em.persist(user);	
	}
	
	@Transactional
	public void update(User user) {
		em.merge(user);	
	}
}
