package com.pavlova.notes.database.entity;

import javax.persistence.*;

@Entity
public class Access {
    @Id
    private String code;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;
    @ManyToOne(fetch = FetchType.EAGER)
    private Note note;

    public Access() {
    }

    public Access(String code, User user, Note note) {
        this.code = code;
        this.user = user;
        this.note = note;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }
}