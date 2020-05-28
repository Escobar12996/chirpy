package com.escobar.chirpy.models.entity;

public class ChatMessage {

    private String content;
    private String sender;
    private Long idReceived;
    private Long idSender;
    
    public String getContent() {
      return content;
    }

    public void setContent(String content) {
      this.content = content;
    }

    public String getSender() {
      return sender;
    }

    public void setSender(String sender) {
      this.sender = sender;
    }

    public Long getIdReceived() {
        return idReceived;
    }

    public void setIdReceived(Long idReceived) {
        this.idReceived = idReceived;
    }

    public Long getIdSender() {
        return idSender;
    }

    public void setIdSender(Long idSender) {
        this.idSender = idSender;
    }
    
    
}
