package com.escobar.chirpy.models.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import com.sun.istack.NotNull;

@Entity
@Table(name = "Emoticons")
@NamedQuery(name="Emoticon.findAll", query="SELECT e FROM Emoticon e")
public class Emoticon implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	private int id;
	
	@NotNull
	@Size(min = 3)
	private String comment;
	
	@NotNull
	@Size(min = 3)
	@Column(unique = true)
	private String command;
	
	@Lob
	private byte[] image;
	
	
	
}
