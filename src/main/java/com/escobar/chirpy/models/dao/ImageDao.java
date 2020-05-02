package com.escobar.chirpy.models.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.escobar.chirpy.models.entity.Image;
import com.escobar.chirpy.models.entity.Publication;
import com.escobar.chirpy.models.entity.User;

@Repository
public class ImageDao {

	@PersistenceContext
    private EntityManager em;

	@Transactional(readOnly=true)
    public Image findById(Long id) {
        TypedQuery<Image> query = em.createQuery("SELECT i FROM Image i WHERE i.id = :id", Image.class); 
        query.setParameter("id", id);
        
        try {
            return query.getSingleResult();

        } catch (Exception e) {
			// TODO: handle exception
        	return null;
		}
    }
	
	@Transactional(readOnly=true)
    public List<Image> findByPubli(Publication publication) {
        TypedQuery<Image> query = em.createQuery("SELECT i FROM Image i WHERE i.publi = :id", Image.class); 
        query.setParameter("id", publication);
        return query.getResultList();
    }
	
	@Transactional(readOnly=true)
    public List<Image> findByUser(User user) {
        TypedQuery<Image> query = em.createQuery("SELECT i FROM Image i WHERE i.user = :id", Image.class); 
        query.setParameter("id", user);
        return query.getResultList();
    }
	
	@Transactional
    public void save(Image image) {
        em.persist(image);	
    }

    @Transactional
    public void update(Image image) {
        em.merge(image);
    }
	
}