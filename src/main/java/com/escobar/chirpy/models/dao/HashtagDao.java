package com.escobar.chirpy.models.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;
import javax.persistence.TypedQuery;
import org.springframework.transaction.annotation.Transactional;

import com.escobar.chirpy.models.entity.Hashtag;
import java.util.Calendar;
import java.util.Date;

@Repository
public class HashtagDao {
	
    @PersistenceContext
    private EntityManager em;

        
    public Hashtag findByHashtagName(String name) {
        TypedQuery<Hashtag> query = em.createQuery("SELECT h FROM Hashtag h WHERE h.hashtagname = :hashtagname", Hashtag.class); 
        query.setParameter("hashtagname", name);
        
        try {
        	return query.getSingleResult();
        } catch (Exception e) {
			return null;
		}
    }
    
    public List<Hashtag> findUp() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, -1);
        System.out.println(calendar.getTime());
        TypedQuery<Hashtag> query = em.createQuery("SELECT h FROM Hashtag h where h.datelast >= :fecha order by h.usos DESC, datelast DESC", Hashtag.class); 
        query.setParameter("fecha", calendar.getTime());
        query.setMaxResults(10);

    	return query.getResultList();
    }
    
    public List<Hashtag> findHastagsName(String name) {
        TypedQuery<Hashtag> query = em.createQuery("SELECT h FROM Hashtag h WHERE h.hashtagname LIKE CONCAT('%',:hashtagname,'%')", Hashtag.class); 
        query.setParameter("hashtagname", name);
        return query.getResultList();

    }
    
    @Transactional
    public void save(Hashtag hashtag) {
        em.persist(hashtag);	
    }
    
    @Transactional
    public void update(Hashtag hashtag) {
        em.merge(hashtag);	
    }
        
}
