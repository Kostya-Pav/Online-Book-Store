package com.onlinebookstore.service;

import com.onlinebookstore.model.Role;
import com.onlinebookstore.model.RoleName;
import com.onlinebookstore.model.User;
import com.onlinebookstore.repository.user.RoleRepository;
import com.onlinebookstore.repository.user.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class TestSetup {
    static final String ADMIN_MAIL = "admin@example.com";
    static final String ADMIN_PASS = "admin1234";
    static final String USER_MAIL = "user@example.com";
    static final String USER_PASS = "user1234";

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void createUsers(List<Long> createdUserIds, List<Long> createdRoleIds) {
        Role adminRole = createRoleIfNotExist(RoleName.ADMIN);

        User admin = createUser(ADMIN_MAIL, ADMIN_PASS, "Admin",
                "Admin", "Some address", adminRole);
        userRepository.save(admin);
        createdUserIds.add(admin.getId());
        createdRoleIds.add(adminRole.getId());

        Role userRole = createRoleIfNotExist(RoleName.USER);

        User user = createUser(USER_MAIL, USER_PASS, "User",
                "User", "Some", userRole);
        userRepository.save(user);
        createdUserIds.add(user.getId());
        createdRoleIds.add(userRole.getId());
    }

    public Role createRoleIfNotExist(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));
    }

    public User createUser(
            String email, String password, String firstName,
            String lastName, String shippingAddress, Role userRole
    ) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setShippingAddress(shippingAddress);
        user.getRoles().add(userRole);
        return user;
    }
}
