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
import com.escobar.chirpy.models.entity.Publication;
import com.escobar.chirpy.models.entity.User;

@Entity
@Table(name = "Images")
@NamedQuery(name="Image.findAll", query="SELECT i FROM Image i")
public class Image implements Serializable{

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean block;
    
	@Lob
    private byte[] images;
	
	@ManyToOne
	@JoinColumn(name="publication_id")
	private Publication publi;

	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
	public Image() {
		super();
	}
	
	public Image(byte[] image) {
		this.images = image;
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
