package com.pavlova.notes.security;

import com.pavlova.notes.database.StorageController;
import com.pavlova.notes.database.entity.Group;
import com.pavlova.notes.database.entity.User;
import com.pavlova.notes.utilities.Flags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AppUserDetailsService implements UserDetailsService {
    @Autowired
    private StorageController storage;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    public boolean newUser(String username, String email, String password) {
        User user = storage.user.getByUsername(username);
        if (user == null) {
            user = new User(username, email, passwordEncoder.encode(password));
            storage.user.save(user);
            Group available = new Group("Notes", user);
            available.onFlag(Flags.DEFAULT.flag() | Flags.ROOT.flag());
            storage.group.save(available);
            return true;
        }
        return false;
    }

    public UserDetails loadUserByUsername(String username) {
        User user = storage.user.getByUsername(username);
        if (user == null)
            throw new UsernameNotFoundException("Unknown username: " + username);

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("user")
                .build();
    }
}
