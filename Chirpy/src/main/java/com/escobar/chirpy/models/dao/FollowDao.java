package com.escobar.chirpy.models.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.escobar.chirpy.models.entity.Follow;
import com.escobar.chirpy.models.entity.User;
import javax.persistence.TypedQuery;

@Repository
public class FollowDao {

	@PersistenceContext
	private EntityManager em;
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<Follow> findAll(){
	    Query query = em.createQuery("SELECT f FROM Follow f");
	    return query.getResultList();
	}
	
	public Follow findFollow(Follow follow) {
            TypedQuery<Follow> query = em.createQuery("SELECT f FROM Follow f WHERE f.user = :user and f.followed = :followed", Follow.class); 
	    query.setParameter("user", follow.getUser());
            query.setParameter("followed", follow.getFollowed());
            
            try {
                return query.getSingleResult();
            } catch (Exception e) {
                return null;
            }
	}
        
        public List<Follow> getUserFollow(User user) {
            TypedQuery<Follow> query = em.createQuery("SELECT f FROM Follow f WHERE f.user = :user", Follow.class); 
	    query.setParameter("user", user);

            return query.getResultList();
           
	}
	
	@Transactional
        public void save(Follow publication) {
            em.persist(publication);	
        }
        
        @Transactional
        public void delete(Follow publication) {
            em.remove(publication);	
        }
}
