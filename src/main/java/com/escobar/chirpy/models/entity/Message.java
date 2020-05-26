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
import java.text.SimpleDateFormat;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "Messages")
@NamedQuery(name="Message.findAll", query="SELECT m from Message m")
public class Message implements Serializable{

	private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "send_id")
    private User send;
    
    @ManyToOne
    @JoinColumn(name = "receive_id")
    private User receive;
    
    @NotEmpty()
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

	public String getDateOfSend() {
            SimpleDateFormat sm = new SimpleDateFormat("dd-MM-yyyy");
            String strDate = sm.format(dateOfSend);
            return strDate;
        }

	public void setDateOfSend(Date dateOfSend) {
		this.dateOfSend = dateOfSend;
	}
}
