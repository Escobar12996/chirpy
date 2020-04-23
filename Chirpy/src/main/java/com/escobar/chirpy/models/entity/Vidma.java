package com.escobar.chirpy.models.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "Vidmas")
@NamedQuery(name="Vidma.findAll", query="SELECT v FROM Vidma v")
public class Vidma implements Serializable{

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean block;
    
    private boolean image;
    
    private String videourl;
    
	@Lob
    private byte[] images;
	
	@ManyToOne
	@JoinColumn(name="publication_id")
	private Publication publi;

	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;

	public Vidma() {
		super();
	}
	
	public Vidma(byte[] image) {
		this.image = true;
		this.images = image;
	}
	
	public Vidma(String video) {
		this.image = false;
		this.videourl = video;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isBlock() {
		return block;
	}

	public void setBlock(boolean block) {
		this.block = block;
	}

	public boolean isImage() {
		return image;
	}

	public void setImage(boolean image) {
		this.image = image;
	}

	public String getVideourl() {
		return videourl;
	}

	public void setVideourl(String videourl) {
		this.videourl = videourl;
	}

	public byte[] getImages() {
		return images;
	}

	public void setImages(byte[] images) {
		this.images = images;
	}

	public Publication getPubli() {
		return publi;
	}

	public void setPubli(Publication publi) {
		this.publi = publi;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	
	
}
