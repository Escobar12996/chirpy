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
    public List<Publication> findByUser(User user) {
        TypedQuery<Publication> query = em.createQuery("SELECT p FROM Publication p WHERE p.user = :user", Publication.class); 
        query.setParameter("user", user);
        return query.getResultList();
    }
    
    public List<Publication> findText(String text) {
            TypedQuery<Publication> query = em.createQuery("SELECT p FROM Publication p WHERE p.publication LIKE CONCAT('%',:text,'%')", Publication.class); 
        query.setParameter("text", text);

        return query.getResultList();
    }
    

    @Transactional
    public void save(Publication publication) {
        em.persist(publication);	
    }
	

}
