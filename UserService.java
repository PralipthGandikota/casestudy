package com.example.insurance_management_system.service;


import com.example.insurance_management_system.model.Role;
import com.example.insurance_management_system.model.User;
import com.example.insurance_management_system.repository.RoleRepository;
import com.example.insurance_management_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
	 @Autowired
	    private UserRepository userRepository;
	    
	    @Autowired
	    private PasswordEncoder passwordEncoder;
	    @Autowired
	    private RoleRepository roleRepository;					
	    
	    @Override
	    //@Transactional(readOnly = true)
	    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	        User user = userRepository.findByUsername(username)
	                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
	        
	        return new org.springframework.security.core.userdetails.User(
	                user.getUsername(),
	                user.getPassword(),
	                user.isEnabled(),
	                true,
	                true,
	                true,
	                getAuthorities(user)
	        );
	    }
	    
	    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
	        return user.getRoles().stream()
	                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
	                .collect(Collectors.toList());
	    }
	    
	    //@Transactional
	    public User registerNewUser(User user, String roleName) {
	        if (userRepository.existsByUsername(user.getUsername())) {
	            throw new RuntimeException("Username is already taken");
	        }
	        
	        if (userRepository.existsByEmail(user.getEmail())) {
	            throw new RuntimeException("Email is already in use");
	        }
	        
	        // Encode the password
	        user.setPassword(passwordEncoder.encode(user.getPassword()));
	        
	        // Find existing role or create a new one if it doesn't exist
	        Role role = roleRepository.findByName(roleName);
	        if (role == null) {
	            role = new Role(roleName);
	            role = roleRepository.save(role); // Save the role first to avoid the transient object error
	        }
	        
	        user.addRole(role);
	        
	        return userRepository.save(user);
	    }
	    
	   // @Transactional(readOnly = true)
	    public Optional<User> findByUsername(String username) {
	        return userRepository.findByUsername(username);
	    }
	    
	   // @Transactional(readOnly = true)
	    public Optional<User> findByEmail(String email) {
	        return userRepository.findByEmail(email);
	    }
	    
	    //@Transactional(readOnly = true)
	    public boolean existsByUsername(String username) {
	        return userRepository.existsByUsername(username);
	    }
	    
	    //@Transactional(readOnly = true)
	    public boolean existsByEmail(String email) {
	        return userRepository.existsByEmail(email);
	    }
	    
	    //@Transactional
	    public User save(User user) {
	        return userRepository.save(user);
	    }

}
