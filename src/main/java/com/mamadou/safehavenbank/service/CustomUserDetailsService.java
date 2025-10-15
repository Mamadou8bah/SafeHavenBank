package com.mamadou.safehavenbank.service;

import com.mamadou.safehavenbank.entity.User;
import com.mamadou.safehavenbank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User>user =userRepository.findByEmailIgnoreCase(email);
        if(user.isPresent()){
            return user.get();
        }
        throw new UsernameNotFoundException("User not found");
    }
}
