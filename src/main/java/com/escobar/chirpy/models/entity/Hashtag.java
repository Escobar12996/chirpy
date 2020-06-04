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
@Table(name = "Hashtags")
@NamedQuery(name="Hashtag.findAll", query="SELECT h FROM Hashtag h")
public class Hashtag implements Serializable{
	
    private static final long serialVersionUID = 1L;

    @Id
    private String hashtagname;
    
    private int applications;
    
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date datelast;

	public String getHashtagname() {
		return hashtagname;
	}

	public void setHashtagname(String hashtagname) {
		this.hashtagname = hashtagname;
	}

	public int getApplications() {
		return applications;
	}

	public void setApplications(int applications) {
		this.applications = applications;
	}

	public Date getDatelast() {
		return datelast;
	}

	public void setDatelast(Date datelast) {
		this.datelast = datelast;
	}
    
    
}
