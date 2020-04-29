package com.escobar.chirpy.models.services;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.escobar.chirpy.models.dao.UserDao;
import com.escobar.chirpy.models.entity.Authority;
import com.escobar.chirpy.models.entity.User;
import com.escobar.chirpy.models.entity.UserAuthority;
import com.escobar.chirpy.models.dao.UserAuthorityDao;


@Service("jpaUserDetailsService")
public class JpaUserDetailsService implements UserDetailsService{

    
	@Autowired
	private UserDao userDao;
        
        @Autowired
	private UserAuthorityDao userAuthDao;
	
	
	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		
		User user = userDao.findByUserName(username);
		
		if (user == null) {
			user = userDao.findEmail(username);
		}
		
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		
                for(UserAuthority userauth : userAuthDao.findByUser(user)){
                    authorities.add(new SimpleGrantedAuthority(userauth.getAuthority().getAuthority()));
                }
                
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), true, user.getNotLocker(), true, user.getEnabled(), authorities);
                
	}
	
	 public void autoLogin(String username, String password) {
        UserDetails userDetails = loadUserByUsername(username);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

    }
}
