/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.escobar.chirpy.models.entity;

import com.escobar.chirpy.models.dao.UserDao;
import com.escobar.chirpy.models.services.PublicationService;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author escob
 */

@Entity
@Table(name = "Publications")
@NamedQuery(name="Publication.findAll", query="SELECT p from Publication p")
public class Publication implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Size(min = 3, max = 4000, message="Debe tener mas de 3 caracteres")
    @NotEmpty(message="Debe de tener algo")
    private String publication;
    
    private boolean view;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date dateOfSend;

    public String getDateOfSend() {
        SimpleDateFormat sm = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = sm.format(dateOfSend);
        return strDate;
    }

    public void setDateOfSend(Date dateOfSend) {
        this.dateOfSend = dateOfSend;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPublication() {
        return publication;
    }

    public void setPublication(String publication) {
        this.publication = publication;
    }

    public boolean isView() {
        return view;
    }

    public void setView(boolean view) {
        this.view = view;
    }
    
        
}
