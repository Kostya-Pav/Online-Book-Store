package com.onlinebookstore.service;

import com.onlinebookstore.repository.RoleTestRepository;
import com.onlinebookstore.repository.UserTestRepository;
import com.onlinebookstore.repository.book.BookRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestCleanup {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserTestRepository userTestRepository;

    @Autowired
    private RoleTestRepository roleTestRepository;

    public void cleanupUsersAfterTest(List<Long> usersId, List<Long> rolesId) {
        if (usersId != null && !usersId.isEmpty()) {
            for (Long id : usersId) {
                if (id != null) {
                    userTestRepository.deleteUserRolesByUserId(id);
                    userTestRepository.deleteUserById(id);
                }
            }
            usersId.clear();
        }

        if (rolesId != null && !rolesId.isEmpty()) {
            for (Long id : rolesId) {
                if (id != null) {
                    roleTestRepository.deleteRoleById(id);
                }
            }
            rolesId.clear();
        }
    }

    public void cleanupBooksAfterTest(List<Long> booksId) {
        if (booksId != null && !booksId.isEmpty()) {
            bookRepository.deleteAllById(booksId);
            booksId.clear();
        }
    }
}
