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
@Table(name = "Follows")
@NamedQuery(name="Follow.findAll", query="SELECT f FROM Follow f")
public class Follow implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Id
    @ManyToOne
    @JoinColumn(name = "followed_id")
    private User followed;

    public Follow() {
    }
    
    public Follow(User user, User followed) {
        this.user = user;
        this.followed = followed;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getFollowed() {
        return followed;
    }

    public void setFollowed(User followed) {
        this.followed = followed;
    }
}
