package com.escobar.chirpy.models.dao;

import com.escobar.chirpy.models.entity.Chat;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.escobar.chirpy.models.entity.User;

@Repository
public class ChatDao {

    @PersistenceContext
    private EntityManager em;
	
    @Transactional(readOnly=true)
    public Chat findMessages(User userOne, User userTwo){
    	TypedQuery<Chat> query = em.createQuery("Select c from Chat c where (c.userone = :userOne and c.usertwo = :userTwo) or (c.userone = :userTwo and c.usertwo = :userOne)", Chat.class);
    	query.setParameter("userOne", userOne);
    	query.setParameter("userTwo", userTwo);
        
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
    
    @Transactional
    public void save(Chat chat) {
        em.persist(chat);	
    }

    @Transactional
    public void update(Chat chat) {
        em.merge(chat);
    }
	
    @Transactional
    public void remove(Chat chat) {
        em.remove(chat);
    }
    
    
}
