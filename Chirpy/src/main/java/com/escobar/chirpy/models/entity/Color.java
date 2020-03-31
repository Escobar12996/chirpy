package com.escobar.chirpy.models.entity;

import java.io.Serializable;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "Colors")
@NamedQuery(name="Color.findAll", query="SELECT c FROM Color c")
public class Color implements Serializable{
	
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    private String color;
    
    private String hexBackground;
    
    private String hexText;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getHexBackground() {
        return hexBackground;
    }

    public void setHexBackground(String hexBackground) {
        this.hexBackground = hexBackground;
    }

    public String getHexText() {
        return hexText;
    }

    public void setHexText(String hexText) {
        this.hexText = hexText;
    }
    
    
}
