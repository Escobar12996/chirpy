package com.escobar.chirpy.models.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.escobar.chirpy.models.entity.Publication;
import com.escobar.chirpy.models.entity.User;

@Repository
public class PublicationDao {

    @PersistenceContext
    private EntityManager em;
	
	
	
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true)
    public List<Publication> findAll(){
        Query query = em.createQuery("SELECT p FROM Publication p");
        return query.getResultList();
    }

    @Transactional(readOnly=true)
    public Publication findById(Long id) {
        TypedQuery<Publication> query = em.createQuery("SELECT p FROM Publication p WHERE p.id = :id", Publication.class); 
        query.setParameter("id", id);
        
        try {
        	return query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
        
    }
    
    @Transactional(readOnly=true)
    public List<Publication> findByUser(User user) {
        TypedQuery<Publication> query = em.createQuery("SELECT p FROM Publication p WHERE p.user = :user and view = true ORDER BY dateOfSend DESC", Publication.class); 
        query.setParameter("user", user);
        return query.getResultList();
    }
    
    @Transactional(readOnly=true)
    public List<Publication> findResponse(Publication id) {
        TypedQuery<Publication> query = em.createQuery("SELECT p FROM Publication p WHERE p.publi = :id", Publication.class); 
        query.setParameter("id", id);
        return query.getResultList();
    }
    
    @Transactional(readOnly=true)
    public List<Publication> findByUsers(List<User> users) {
        TypedQuery<Publication> query = em.createQuery("SELECT p FROM Publication p WHERE p.user in :users and view = true ORDER BY dateOfSend DESC", Publication.class); 
        query.setParameter("users", users);
        return query.setMaxResults(50).getResultList();
    }
    
    @Transactional(readOnly=true)
    public List<Publication> findByUsersNext(List<User> users, Long lastId) {
        TypedQuery<Publication> query = em.createQuery("SELECT p FROM Publication p WHERE p.id > :lastId and p.user in :users and view = true ORDER BY dateOfSend DESC", Publication.class); 
        query.setParameter("users", users);
        query.setParameter("lastId", lastId);
        return query.setMaxResults(50).getResultList();
    }
    
    public List<Publication> findText(String text) {
            TypedQuery<Publication> query = em.createQuery("SELECT p FROM Publication p WHERE p.publication LIKE CONCAT('%',:text,'%') and view = true", Publication.class); 
        query.setParameter("text", text);

        return query.getResultList();
    }
    
    @Transactional(readOnly=true)
    public Publication findByUserAndId(Publication publi) {
        TypedQuery<Publication> query = em.createQuery("SELECT p FROM Publication p WHERE p.user = :user and id = :id", Publication.class); 
        query.setParameter("user", publi.getUser());
        query.setParameter("id", publi.getId());
        
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
        
    }

    @Transactional
    public void save(Publication publication) {
        em.persist(publication);	
    }

    @Transactional
    public void update(Publication publication) {
        em.merge(publication);
    }
	

}
