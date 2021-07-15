package com.pavlova.notes.database;

import com.pavlova.notes.database.entity.Access;
import com.pavlova.notes.database.entity.Note;
import com.pavlova.notes.database.entity.Group;
import com.pavlova.notes.database.entity.User;
import com.pavlova.notes.database.repository.AccessRepository;
import com.pavlova.notes.database.repository.GroupRepository;
import com.pavlova.notes.database.repository.NoteRepository;
import com.pavlova.notes.database.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Component
public class StorageController {
    public MessageDigest md5Digest;


    public StorageController() {
        try {
            md5Digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public synchronized String md5(String input) {
        return Base64.getEncoder().encodeToString(md5Digest.digest(input.getBytes(StandardCharsets.UTF_8)));
    }


    @Autowired
    public GroupRepository group;
    @Autowired
    public NoteRepository note;
    @Autowired
    public UserRepository user;
    @Autowired
    public AccessRepository access;

    private static String rndInsert(String str, String ... ins) {
        Random random = new Random(new Date().getTime());
        for (String in : ins) {
            int pos = ((int) random.nextLong()) % str.length();
            str = str.substring(0, pos) + in + str.substring(pos);
        }
        return str;
    }
    public void removeNote(Note nt) {
        List<Access> acc = access.getByNote(nt);
        access.deleteAll(acc);
        note.delete(nt);
    }
    public void updateNote(Note nt) {
        nt.setUpdateDate(new Date());
        note.save(nt);
    }

}
