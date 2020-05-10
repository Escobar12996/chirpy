/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.escobar.chirpy.models.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 *
 * @author escob
 */

@Entity
@Table(name = "Users")
@NamedQuery(name="User.findAll", query="SELECT u FROM User u")
public class User implements Serializable {
	
    private static final long serialVersionUID = 1L;
    public static final int passwordmax = 30;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 3, max = 30)
    @NotEmpty
    @Column(unique = true)
    private String username;

    @Size(min = 3)
    @NotEmpty
    private String name;

    @NotEmpty
    @Column(unique = true)
    private String email;

    @Size(min = 7)
    @NotEmpty
    private String password;

    private Boolean enabled;
    
    private Boolean systenBan;
    
    private Boolean notLocker;
    
    @Lob
    private byte[] imageperf;
    
    @Lob
    private byte[] imagesu;
    
    @Size(max = 1000)
    private String description;
    
    private int quotes;

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
    public Boolean getSystenBan() {
		return systenBan;
	}

	public void setSystenBan(Boolean systenBan) {
		this.systenBan = systenBan;
	}

	public Boolean getNotLocker() {
        return notLocker;
    }

    public void setNotLocker(Boolean notLocker) {
        this.notLocker = notLocker;
    }

    public byte[] getImageperf() {
        return imageperf;
    }

    public void setImageperf(byte[] imageperf) {
        this.imageperf = imageperf;
    }

    public byte[] getImagesu() {
        return imagesu;
    }

    public void setImagesu(byte[] imagesu) {
        this.imagesu = imagesu;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuotes() {
        return quotes;
    }

    public void setQuotes(int quotes) {
        this.quotes = quotes;
    }
	
}

