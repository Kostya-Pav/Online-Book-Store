package com.onlinebookstore.service;

import com.onlinebookstore.model.Role;
import com.onlinebookstore.model.RoleName;
import com.onlinebookstore.model.User;
import com.onlinebookstore.repository.user.RoleRepository;
import com.onlinebookstore.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public User register(User user)
            throws IllegalArgumentException {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Can't register user. Email already exist.");
        }
        Role defaultRole = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        user.getRoles().add(defaultRole);
        return userRepository.save(user);
    }
}
