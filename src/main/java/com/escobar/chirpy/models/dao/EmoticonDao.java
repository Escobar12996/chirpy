package com.escobar.chirpy.models.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.escobar.chirpy.models.entity.Emoticon;

@Repository
public class EmoticonDao {

	@PersistenceContext
	private EntityManager em;
        
	public Emoticon findemoticon(String name) {
        TypedQuery<Emoticon> query = em.createQuery("SELECT e FROM Emoticon e WHERE e.command = :command", Emoticon.class); 
        query.setParameter("command", name);
        
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
	}
	
	public Emoticon findemoticonId(Long id) {
        TypedQuery<Emoticon> query = em.createQuery("SELECT e FROM Emoticon e WHERE e.id = "+id+"", Emoticon.class); 
        /*query.setParameter("id", id);*/
        
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
	}
	
	@Transactional
	public void save(Emoticon emoticon) {
		em.persist(emoticon);	
	}
	
	@Transactional
	public void update(Emoticon emoticon) {
		em.merge(emoticon);	
	}
	
	@Transactional
	public void remove(Emoticon emoticon) {
		em.remove(emoticon);
	}
}

