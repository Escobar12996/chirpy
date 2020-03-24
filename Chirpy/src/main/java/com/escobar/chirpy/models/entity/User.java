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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 3, max = 30, message="Debe tener mas de 3 letras y menor de 30")
    @NotEmpty(message="Debe indicar el Nombre de Usuario")
    @Column(unique = true)
    private String username;

    @Size(min = 3, message="Debe tener mas de 3 letras")
    @NotEmpty(message="Debe indicar su nombre y su apellido")
    private String namealasname;


    @NotEmpty(message="Debe indicar su correo")
    @Column(unique = true)
    private String email;

    @Size(min = 7, message="Debe tener entre 8 o mas letras")
    @NotEmpty(message="Debe indicar una contraseÃ±a")
    private String password;

    private Boolean enabled;
    
    private Boolean notLocker;
    
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

    public String getNamealasname() {
        return namealasname;
    }

    public void setNamealasname(String namealasname) {
        this.namealasname = namealasname;
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
    
    public Boolean getNotLocker() {
        return notLocker;
    }

    public void setNotLocker(Boolean notLocker) {
        this.notLocker = notLocker;
    }
}

