package com.pavlova.notes.database.repository;

import com.pavlova.notes.database.entity.Access;
import com.pavlova.notes.database.entity.Note;
import com.pavlova.notes.database.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccessRepository extends JpaRepository<Access, String>  {
    List<Access> getByUser(User user);
    List<Access> getByNote(Note note);

}
