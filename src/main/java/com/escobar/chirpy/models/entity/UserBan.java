package com.escobar.chirpy.models.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "Users_Ban")
@NamedQuery(name="UserBan.findAll", query="SELECT u FROM UserBan u")
public class UserBan implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
    @ManyToOne
    @JoinColumn(name = "user_send_ban")
	private User userSendBan;
	
	@Id
    @ManyToOne
    @JoinColumn(name = "user_received_ban")
	private User userReceivedBan;
	
	@Id
	@ManyToOne
    @JoinColumn(name = "publication_id")
	private Publication publi;
	
	@JsonFormat(pattern="yyyy-MM-dd")
    private Date dateOfSend;
	
	
	public UserBan() {
		super();
	}
	
	
	public UserBan(User usersend, User userreceived, Publication publi) {
		this.userSendBan = usersend;
		this.userReceivedBan = userreceived;
		this.publi = publi;
		this.dateOfSend = new Date();
	}
	
	public User getUserSendBan() {
		return userSendBan;
	}

	public void setUserSendBan(User userSendBan) {
		this.userSendBan = userSendBan;
	}

	public User getUserReceivedBan() {
		return userReceivedBan;
	}

	public void setUserReceivedBan(User userReceivedBan) {
		this.userReceivedBan = userReceivedBan;
	}

	public Date getDateOfSend() {
		return dateOfSend;
	}

	public void setDateOfSend(Date dateOfSend) {
		this.dateOfSend = dateOfSend;
	}

	public Publication getPubli() {
		return publi;
	}

	public void setPubli(Publication publi) {
		this.publi = publi;
	}
}
