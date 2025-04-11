package com.onlinebookstore.service;

import com.onlinebookstore.model.User;
import com.onlinebookstore.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User register(User user)
            throws IllegalArgumentException {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Can't register user");
        }
        return userRepository.save(user);
    }
}
