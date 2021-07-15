package com.pavlova.notes.webinterface;

import com.pavlova.notes.database.StorageController;
import com.pavlova.notes.database.entity.Group;
import com.pavlova.notes.database.entity.Note;
import com.pavlova.notes.database.entity.User;
import com.pavlova.notes.utilities.Flags;
import com.pavlova.notes.webinterface.view.NoteView;
import com.pavlova.notes.webinterface.view.SimpleViewAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@Controller
public class Api {
    @Autowired
    StorageController storage;

    private static boolean access(User user, Optional instance) {
        if (instance.isPresent()) {
            if (instance.get() instanceof Group)
                return access(user, (Group) instance.get());
            if (instance.get() instanceof Note)
                return access(user, (Note) instance.get());
        }
        return false;
    }
    private static boolean access(User user, Group group) {
        return group.getUser() == user;
    }


    private static boolean access(User user, Note note) {
        return access(user, note.getGroup());
    }

    @GetMapping("/request/save_note")
    public ResponseEntity<SimpleViewAdapter<String>> saveNote(
            @RequestParam("id") Long id,
            @RequestParam("name") String name,
            @RequestParam("body") String body,
            Principal principal) {

        User user = storage.user.getByUsername(principal.getName());

        if (id != null) {
            Optional<Note> note = storage.note.findById(id);
            if (access(user, note)) {
                Note edit = note.get();
                edit.setName(name);
                edit.setContent(body);
                storage.updateNote(edit);
                return ResponseEntity.ok(new SimpleViewAdapter<String>(edit.getDescription()));
            }
        }

        return ResponseEntity.badRequest().body(new SimpleViewAdapter<>("error"));
    }


    @PostMapping("/request/save/{noteId}")
    public String edit(
            @PathVariable Long noteId,
            @ModelAttribute("note") NoteView form,
            Principal principal) {
        Optional<Note> selected = storage.note.findById(noteId);

        if (!selected.isPresent())
            return "redirect:/home";

        Note note = selected.get();
        Group group = note.getGroup();

        if (group == null)
            return "redirect:/home";

        User user = storage.user.getByUsername(principal.getName());
        if (access(user, group)) {
            note.setName(form.getName());
            note.setContent(form.getContent());
            storage.updateNote(note);
        }

        return "redirect:/home/" + group.getId() + "/" + note.getId();
    }

    @GetMapping("/request/new_note/{selectedGroup}")
    public String createNote(@PathVariable Long selectedGroup, Principal principal) {

        User user = storage.user.getByUsername(principal.getName());
        if (selectedGroup == null) {
            return "redirect:/home";
        }
        Optional<Group> group = storage.group.findById(selectedGroup);
        if (access(user, group)) {
            Note note = new Note("", "", group.get());
            storage.note.save(note);
            return "redirect:/home/"+selectedGroup+"/"+note.getId();
        }
        return "redirect:/home";
    }

    @GetMapping("/request/new_group")
    public ResponseEntity<String> createGroup(@RequestParam("name") String name, Principal principal) {
        User user = storage.user.getByUsername(principal.getName());
        if (name != null) {
            Group group = new Group(name, user);
            storage.group.save(group);
            return ResponseEntity.ok("/home/"+group.getId() + "/-1");
        }


        return ResponseEntity.badRequest().body("/home");
    }

    @GetMapping("/request/rename_group")
    public ResponseEntity<String> renameGroup(@RequestParam("id") Long id, @RequestParam("name") String name, Principal principal) {
        User user = storage.user.getByUsername(principal.getName());

        if ((id != null) && (name != null)) {
            Optional<Group> group = storage.group.findById(id);
            if (access(user, group)) {
                if (group.get().testFlag(Flags.NO_RENAME))
                    return ResponseEntity.unprocessableEntity().body("NO RENAME");

                group.get().setName(name);
                storage.group.save(group.get());
                return ResponseEntity.ok(group.get().getName());
            }
            return ResponseEntity.unprocessableEntity().body("NO ACCESS");
        }

        return ResponseEntity.badRequest().body("Parameter error");
    }

    @GetMapping("/request/remove_group")
    public ResponseEntity<String> removeGroup(@RequestParam("id") Long id, Principal principal) {
        User user = storage.user.getByUsername(principal.getName());

        if (id != null) {
            Optional<Group> group = storage.group.findById(id);
            if (access(user, group)) {
                storage.group.delete(group.get());
                return ResponseEntity.ok("");
            }
        }
        return ResponseEntity.badRequest().body("/home");
    }

    @GetMapping("/request/remove_note")
    public ResponseEntity<String> removeNote(@RequestParam("id") Long id, Principal principal) {
        User user = storage.user.getByUsername(principal.getName());

        if (id != null) {
            Optional<Note> note = storage.note.findById(id);
            if (access(user, note)) {
                storage.removeNote(note.get());
                return ResponseEntity.ok("");
            }
        }

        return ResponseEntity.badRequest().body("/home");
    }

    @GetMapping("/request/group_size")
    public ResponseEntity<SimpleViewAdapter<Integer>> getSize(@RequestParam("id") Long id, Principal principal) {
        User user = storage.user.getByUsername(principal.getName());

        if (id != null) {
            Optional<Group> group = storage.group.findById(id);
            if (access(user, group)) {
                return ResponseEntity.ok(new SimpleViewAdapter<>(group.get().getNotes().size()));
            }
        }

        return ResponseEntity.badRequest().body(new SimpleViewAdapter<>(-1));
    }
}
