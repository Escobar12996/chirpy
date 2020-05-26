package com.escobar.chirpy.models.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.escobar.chirpy.models.entity.User;
import com.escobar.chirpy.models.entity.Message;

@Repository
public class MessagesDao {

    @PersistenceContext
    private EntityManager em;
	
    @Transactional(readOnly=true)
    public List<Message> findChat(User userOne, User userTwo){
    	TypedQuery<Message> query = em.createQuery("SELECT m FROM Message m where (m.send = :userOne and m.receive = :userTwo) or (m.send = :userTwo and m.receive = :userOne)", Message.class);
    	query.setParameter("userOne", userOne);
    	query.setParameter("userTwo", userTwo);
    	return query.getResultList();
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
