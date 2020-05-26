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
@Table(name = "Chats")
@NamedQuery(name="Chat.findAll", query="SELECT c from Chat c")
public class Chat implements Serializable{

	private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "userone_id")
    private User userone;
    
    @ManyToOne
    @JoinColumn(name = "usertwo_id")
    private User usertwo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUserone() {
        return userone;
    }

    public void setUserone(User userone) {
        this.userone = userone;
    }

    public User getUsertwo() {
        return usertwo;
    }

    public void setUsertwo(User usertwo) {
        this.usertwo = usertwo;
    }

	
}
