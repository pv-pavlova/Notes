package com.pavlova.notes.database.repository;

import com.pavlova.notes.database.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {
    User getByUsername(String username);
}
