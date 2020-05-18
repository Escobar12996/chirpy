/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.escobar.chirpy.models.services;

import com.escobar.chirpy.models.dao.EmoticonDao;
import com.escobar.chirpy.models.dao.HashtagDao;
import com.escobar.chirpy.models.dao.HashtagPublicationDao;
import com.escobar.chirpy.models.dao.ImageDao;
import com.escobar.chirpy.models.dao.PublicationDao;
import com.escobar.chirpy.models.dao.UserDao;
import com.escobar.chirpy.models.dao.UserQuotePublicationDao;
import com.escobar.chirpy.models.entity.User;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.unbescape.html.HtmlEscape;

import com.escobar.chirpy.models.entity.Emoticon;
import com.escobar.chirpy.models.entity.Hashtag;
import com.escobar.chirpy.models.entity.HashtagPublication;
import com.escobar.chirpy.models.entity.Image;
import com.escobar.chirpy.models.entity.Publication;
import com.escobar.chirpy.models.entity.UserQuotePublication;
import com.escobar.chirpy.models.miscellaneous.ImageResizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author escob
 */

@Service("PublicationFormated")
public class PublicationService {
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private PublicationDao publicationDao;
    
    @Autowired
    private HashtagPublicationDao hashtagPublicationDao;
    
    @Autowired
    private HashtagDao hashtagDao;
    
    @Autowired
    private ImageDao imageDao;
    
    @Autowired
    private EmoticonDao emoticonDao;
    
    @Autowired
    private UserQuotePublicationDao userQuotePublicationDao;
    
    private Pattern patternuserone = Pattern.compile("@&quot;[A-Za-z0-9\\s]+&quot;");
    private Pattern patternusertwo = Pattern.compile("@[A-Za-z0-9]+\\s?");
    private Pattern patternhastags = Pattern.compile("#[A-Za-z0-9_-]+\\s?");
    private Pattern patternemoticon = Pattern.compile(":[A-Za-z0-9\\s]+:");
    
    public void formatedAndSave(Publication publi, MultipartFile files[]) {
        
        List<User> userlist = new ArrayList<User>();
        List<String> hashtags = new ArrayList<>();
        publi.setDateOfSend(new Date());
        publi.setView(true);

        String publication = HtmlEscape.escapeHtml5(publi.getPublication());
        
        //comprobamos si tiene alguna mencion, hastag o emoji
        if (publication.contains("@") || publication.contains("#") || publication.contains(":")){
            
            Matcher matcheruserone = patternuserone.matcher(publication);
            Matcher matcherusertwo = patternusertwo.matcher(publication);
            Matcher matcherhastags = patternhastags.matcher(publication);
            Matcher matcheremoticon = patternemoticon.matcher(publication);
            
            boolean bandera = false;
            
            while (!bandera){
                
                if (matcheruserone.find()) {
                    String user = matcheruserone.group().replace("@&quot;", "");
                    user = user.replace("&quot;", "");
                    User userc = userDao.findByUserName(user);
                    
                    if (userc != null){
                        userlist.add(userc);
                        publication = publication.replace(matcheruserone.group(), "<a href=\"/profile/"+userc.getId()+"\">@"+userc.getUsername()+"</a>"); 
                    }
                    
                } else if (matcherusertwo.find()){

                    String user = matcherusertwo.group().replace("@", "");
                    user = user.replace(" ", "");
                    User userc = userDao.findByUserName(user);
                    
                    if (userc != null){
                        userlist.add(userc);
                        publication = publication.replace(matcherusertwo.group(), "<a href=\"/profile/"+userc.getId()+"\">@"+userc.getUsername()+"</a> "); 
                    }
                    
                } else if (matcherhastags.find()){

                    String user = matcherhastags.group().replace("#", "");
                    user = user.trim();
                    hashtags.add(user);
                    
                    publication = publication.replace(matcherhastags.group(), "<a href=\"/explorer?find="+user+"\">"+matcherhastags.group()+"</a> "); 
                    
                } else if (matcheremoticon.find()){

                    String user = matcheremoticon.group().replace(":", "");
                    user = user.trim();
                    
                    Emoticon emoti = emoticonDao.findemoticon(user);
                    
                    if (emoti != null) {
                        publication = publication.replace(matcheremoticon.group(), "<img src=\"/image/emoji/"+ emoti.getId() +"\" alt=\""+emoti.getComment()+"\">"); 
                    }
                    
                } else {
                    bandera = true;
                }
            }
        }
        
        publi.setPublication(publication);
        publicationDao.save(publi);
        
        for(User u: userlist){
            UserQuotePublication uqp = new UserQuotePublication();
            uqp.setPublication(publi);
            uqp.setUser(u);
            try {
                userQuotePublicationDao.save(uqp);
                u.setQuotes(u.getQuotes()+1);
                userDao.update(u);
            } catch (Exception e) {}
        }
        
        for(String h: hashtags){
        	
        	Hashtag ha = new Hashtag();
        	ha.setDatelast(new Date());
        	ha.setHashtagname(h);
        	ha.setUsos(1);
        	
        	try {
        		hashtagDao.save(ha);
        	} catch (Exception e) {
        		ha = hashtagDao.findByHashtagName(ha.getHashtagname());
        		ha.setUsos(ha.getUsos()+1);
        		ha.setDatelast(new Date());
        		hashtagDao.update(ha);
        	}
        	
        	try {
        		HashtagPublication hp = new HashtagPublication();
        		hp.setHashtag(ha);
        		hp.setPublication(publi);
        		hashtagPublicationDao.save(hp);
        	} catch (Exception e) {}
            
        }
        
        if (files != null) {
        	for(int i = 0; i < files.length; i++) {
            	MultipartFile file = files[i];
            	if (file != null && !file.isEmpty()) {
            		
            		try {
            			Image v = new Image(file.getBytes());
                        v.setPubli(publi);
                        v.setUser(publi.getUser());
                        v.setView(true);
                        imageDao.save(v);
                    } catch (IOException e) {}
            		
            	}
            }
        }
        
    }
}
