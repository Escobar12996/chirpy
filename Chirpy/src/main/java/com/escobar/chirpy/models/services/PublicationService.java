/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.escobar.chirpy.models.services;

import com.escobar.chirpy.models.dao.UserDao;
import com.escobar.chirpy.models.dao.UserQuotePublicationDao;
import com.escobar.chirpy.models.entity.User;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.unbescape.html.HtmlEscape;

/**
 *
 * @author escob
 */

@Service("PublicationFormated")
public class PublicationService {
    
    @Autowired
    private UserDao userdao;
    
    @Autowired
    private UserQuotePublicationDao userQuotePublicationDao;
    
    private Pattern patternuserone = Pattern.compile("@&quot;[A-Za-z0-9\\s]+&quot;");
    private Pattern patternusertwo = Pattern.compile("@[A-Za-z0-9]+\\s?");
    private Pattern patternhastags = Pattern.compile("#[A-Za-z0-9_-]+\\s?");
    
    
    public String formated(String publication) {
        
        publication = HtmlEscape.escapeHtml5(publication);
        
        //comprobamos si tiene alguna mencion
        if (publication.contains("@")){
            
            Matcher matcheruserone = patternuserone.matcher(publication);
            Matcher matcherusertwo = patternusertwo.matcher(publication);
            Matcher matcherhastags = patternhastags.matcher(publication);
            
            boolean bandera = false;
            
            while (!bandera){
                
                if (matcheruserone.find()) {
                    String user = matcheruserone.group().replace("@&quot;", "");
                    user = user.replace("&quot;", "");
                    User userc = userdao.findByUserName(user);
                    
                    if (userc != null){
                        publication = publication.replace(matcheruserone.group(), "<a href=\"/userdetails/"+userc.getId()+"\">@"+userc.getUsername()+"</a>"); 
                    }
                    
                } else if (matcherusertwo.find()){

                    String user = matcherusertwo.group().replace("@", "");
                    user = user.replace(" ", "");
                    User userc = userdao.findByUserName(user);
                    
                    if (userc != null){
                        publication = publication.replace(matcherusertwo.group(), "<a href=\"/userdetails/"+userc.getId()+"\">@"+userc.getUsername()+"</a> "); 
                    }
                    
                } else if (matcherhastags.find()){

                    String user = matcherhastags.group().replace("#", "");
                    user = user.replace(" ", "");
                    publication = publication.replace(matcherhastags.group(), "<a href=\"/home\">"+matcherhastags.group()+"</a> "); 
                    
                } else {
                    bandera = true;
                }
            }
        }
        
        return publication;
    }
}
