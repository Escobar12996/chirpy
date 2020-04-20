/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.escobar.chirpy.models.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author escob
 */

@Entity
@Table(name = "hashtag_publication")
@NamedQuery(name="HashtagPublication.findAll", query="SELECT h FROM HashtagPublication h")
public class HashtagPublication implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @ManyToOne
    @JoinColumn(name = "Hashtag_id")
    private Hashtag hashtag;
    
    @Id
    @ManyToOne
    @JoinColumn(name = "publication_id")
    private Publication publication;

    public Hashtag getHashtag() {
		return hashtag;
	}

	public void setHashtag(Hashtag hashtag) {
		this.hashtag = hashtag;
	}

	public Publication getPublication() {
        return publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }
    
    
    
    
}
