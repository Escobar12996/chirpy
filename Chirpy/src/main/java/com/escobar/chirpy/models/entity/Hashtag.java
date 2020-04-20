package com.escobar.chirpy.models.entity;

import java.io.Serializable;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "Hashtags")
@NamedQuery(name="Hashtag.findAll", query="SELECT u FROM Hashtag u")
public class Hashtag implements Serializable{
	
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private String name;
    
    private int usos;
    

}
