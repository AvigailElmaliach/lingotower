package com.lingotower.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.lingotower.data.AdminRepository;
import com.lingotower.data.UserRepository;
import com.lingotower.model.Admin;
import com.lingotower.model.BaseUser;
import com.lingotower.model.User;

@Service
	public class MyUserDetailsService implements UserDetailsService {

	
	
	 @Autowired
	    private UserRepository userRepository;

	    @Autowired
	    private AdminRepository adminRepository;
	    
	    @Override
	    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	        User user = userRepository.findByUsername(username)
	                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

	        Admin admin = adminRepository.findByUsername(username)
	                .orElse(null);  // אם לא נמצא, admin יהיה null

	        if (admin != null) {
	            return new UserPrincipal((BaseUser) admin); // Cast לאובייקט BaseUser
	        }

	        return new UserPrincipal(user);
	    }

//	    @Override
//	    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//	        // תחילה ננסה למצוא את המשתמש מתוך ריפוזיטוריית ה-user
//	        User user = userRepository.findByUsername(username)
//	                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//	        
//	        Admin admin = adminRepository.findByUsername(username)
//	                .orElse(null);  // אם לא נמצא, admin יהיה null
//
//	        // אם יש לנו admin, נמיר אותו ל-BaseUser
//	        if (admin != null) {
//	            return new UserPrincipal((BaseUser) admin); // Cast לאובייקט BaseUser
//	        }
//
//	        // אם יש לנו user, פשוט נחזיר אותו
//	        return new UserPrincipal(user); // אם לא נמצא admin, נחזיר את ה-user כרגיל
//	    }
//	    @Autowired
//	    private UserRepository userRepository;
//	  
//
//	    @Override
//	    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//	        User user = userRepository.findByUsername(username)
//	                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//	        // יצירת UserDetails על פי סוג המשתמש
//	        return new UserPrincipal(user);
//	    }
	}

