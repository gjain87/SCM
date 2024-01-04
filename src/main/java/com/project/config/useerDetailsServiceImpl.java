package com.project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.project.dao.userRepository;
import com.project.entities.User;

public class useerDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private userRepository dao;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	//fetching user from database
		User user = dao.getUserByUserName(username);
		
		if(user==null)
		{
			throw new UsernameNotFoundException("User not found!!");
		}
		
		customUserDetails cud=new customUserDetails(user); 
		return cud;
	}

}
