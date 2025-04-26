package com.onlinebookstore.service;

import com.onlinebookstore.exeption.UsernameConflictException;
import com.onlinebookstore.model.User;

public interface UserService {
    User register(User user) throws UsernameConflictException;
}
