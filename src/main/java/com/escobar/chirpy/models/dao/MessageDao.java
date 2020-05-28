package com.escobar.chirpy.models.dao;

import com.escobar.chirpy.models.entity.Chat;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.escobar.chirpy.models.entity.User;
import com.escobar.chirpy.models.entity.Message;

@Repository
public class MessageDao {

    @PersistenceContext
    private EntityManager em;
	
    @Transactional(readOnly=true)
    public List<Message> findMessages(User userOne, User userTwo){
    	TypedQuery<Message> query = em.createQuery("SELECT m FROM Message m where m.chat = (select c.id from Chat c where (c.userone = :userOne and c.usertwo = :userTwo) or (c.userone = :userTwo and c.usertwo = :userOne))", Message.class);
    	query.setParameter("userOne", userOne);
    	query.setParameter("userTwo", userTwo);
    	return query.getResultList();
    }
    
    public Message findLastMessageFromChat(Chat chat){
    	TypedQuery<Message> query = em.createQuery("SELECT m FROM Message m where m.chat = :chat order by m.id desc", Message.class);
    	query.setParameter("chat", chat);
    	
        try {
            return query.setMaxResults(1).getSingleResult();
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
    
    @Transactional
    public void save(Message message) {
        em.persist(message);	
    }

    @Transactional
    public void update(Message message) {
        em.merge(message);
    }
	
    @Transactional
    public void remove(Message message) {
        em.remove(message);
    }
    
    
}
