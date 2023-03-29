package com.example.demo.config;

import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userByUserName = userRepository.getUserByUserName(username);
        if(userByUserName==null){
            throw new UsernameNotFoundException("User not found !!");
        }
        CustomUserDetails customUserDetails = new CustomUserDetails(userByUserName);
        return customUserDetails;
    }
}
