package com.escobar.chirpy.models.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name="confirmation_tokens")
@NamedQuery(name="ConfirmationToken.findAll", query="SELECT c FROM ConfirmationToken c")
public class ConfirmationToken  implements Serializable {
    
    private static final long serialVersionUID = 1L;
	
	 @Id
	 @GeneratedValue(strategy=GenerationType.AUTO)
	 private Long id;

	 @Column(name="confirmation_token")
	 private String confirmationToken;

	 @OneToOne(targetEntity=User.class, fetch=FetchType.EAGER)
	 @JoinColumn(nullable = false, name = "user_id")
	 private User user;
	 
	 public ConfirmationToken() {}
 
	 public ConfirmationToken(User user) {
	      this.user = user;
	      confirmationToken = UUID.randomUUID().toString();
	 }

	public Long getId() {
		return id;
	}

	public void setTokenid(Long id) {
		this.id = id;
	}

	public String getConfirmationToken() {
		return confirmationToken;
	}

	public void setConfirmationToken(String confirmationToken) {
		this.confirmationToken = confirmationToken;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	 
}
