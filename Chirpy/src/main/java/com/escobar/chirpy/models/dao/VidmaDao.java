package com.escobar.chirpy.models.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.escobar.chirpy.models.entity.Publication;
import com.escobar.chirpy.models.entity.Vidma;

@Repository
public class VidmaDao {

	@PersistenceContext
    private EntityManager em;
	
	@Transactional
	public void save(Vidma vidma){
	    em.persist(vidma);
	}
	
	public Vidma findById(Long id) {
		TypedQuery<Vidma> query = em.createQuery("SELECT v FROM Vidma v WHERE v.id = :id", Vidma.class); 
	    query.setParameter("id", id);
	        
	    try {
	        return query.getSingleResult();
	    } catch (Exception e) {
	        return null;
	    }
	}
	
	public List<Vidma> findByPublication(Long id) {
		TypedQuery<Vidma> query = em.createQuery("SELECT v FROM Vidma v WHERE v.publi = "+ id +"", Vidma.class); 
		//query.setParameter("id", id);
		
	    return query.getResultList();
	}
	
	public List<Vidma> findByUser(Long id) {
		TypedQuery<Vidma> query = em.createQuery("SELECT v FROM Vidma v WHERE v.user = "+ id +"", Vidma.class); 
		
	    return query.getResultList();
	}
}