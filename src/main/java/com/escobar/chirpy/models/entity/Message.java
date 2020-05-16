package com.escobar.chirpy.models.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "Messages")
@NamedQuery(name="Message.findAll", query="SELECT m from Message m")
public class Message implements Serializable{

	private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
    private User send;
    
    private User receive;
    
    private String text;
    
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date dateOfSend;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getSend() {
		return send;
	}

	public void setSend(User send) {
		this.send = send;
	}

	public User getReceive() {
		return receive;
	}

	public void setReceive(User receive) {
		this.receive = receive;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getDateOfSend() {
		return dateOfSend;
	}

	public void setDateOfSend(Date dateOfSend) {
		this.dateOfSend = dateOfSend;
	}
}
