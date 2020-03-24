package com.escobar.chirpy.models.entity;

import java.io.Serializable;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "Authorities")
@NamedQuery(name="Authority.findAll", query="SELECT u FROM Authority u")
public class Authority implements Serializable{
	
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String authority;

    public Long getId() {
            return id;
    }

    public void setId(Long id) {
            this.id = id;
    }

    public String getAuthority() {
            return authority;
    }

    public void setAuthority(String authority) {
            this.authority = authority;
    }
}
