package com.auth.service.service.impl;

import com.auth.service.model.Users;
import com.auth.service.repo.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        java.util.Set<GrantedAuthority> authorities = new java.util.HashSet<>();
        if (user.getRoles() != null) {
            for (com.auth.service.model.Roles r : user.getRoles()) {
                authorities.add(new SimpleGrantedAuthority(r.getRole()));
                if (r.getPermissions() != null) {
                    r.getPermissions().forEach(p -> authorities.add(new SimpleGrantedAuthority(p.getPermission())));
                }
            }
        }
        return new User(user.getUsername(), user.getPassword(), true, true, true, true, authorities);
    }
}
