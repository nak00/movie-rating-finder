package com.example.movieratingfinder.security;

import com.example.movieratingfinder.model.User;
import com.example.movieratingfinder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try to find by username first
        Optional<User> userOpt = userRepository.findByUsername(username);

        // If not found, try by email
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(username);
        }

        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username or email: " + username);
        }

        User user = userOpt.get();

        // Check if account is verified for local users
        if ("local".equals(user.getProvider()) && !user.isEnabled()) {
            throw new UsernameNotFoundException("Account not verified. Please check your email.");
        }

        List<SimpleGrantedAuthority> authorities = Arrays.stream(user.getRoles().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                true, // account non-expired
                true, // credentials non-expired
                true, // account non-locked
                authorities
        );
    }
}
