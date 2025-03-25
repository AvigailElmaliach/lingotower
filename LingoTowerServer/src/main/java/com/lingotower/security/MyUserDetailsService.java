package com.lingotower.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.lingotower.data.UserRepository;
import com.lingotower.model.User;

@Service
	public class MyUserDetailsService implements UserDetailsService {

	    @Autowired
	    private UserRepository userRepository;

	    @Override
	    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	        User user = userRepository.findByUsername(username)
	                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

	        // יצירת UserDetails על פי סוג המשתמש
	        return new UserPrincipal(user);
	    }
	}

