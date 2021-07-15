package com.pavlova.notes.database.repository;

import com.pavlova.notes.database.entity.Group;
import com.pavlova.notes.database.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.*;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findAllByGroupInAndNameLikeOrderByName(List<Group> groups, String name);
}
