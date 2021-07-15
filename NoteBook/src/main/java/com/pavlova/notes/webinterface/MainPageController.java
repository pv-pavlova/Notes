package com.pavlova.notes.webinterface;

import com.pavlova.notes.database.StorageController;
import com.pavlova.notes.database.entity.Group;
import com.pavlova.notes.database.entity.Note;
import com.pavlova.notes.database.entity.User;
import com.pavlova.notes.utilities.Flags;
import com.pavlova.notes.webinterface.view.NoteView;
import com.pavlova.notes.webinterface.view.SearchView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.security.Principal;
import java.util.*;
import static java.util.Collections.sort;

@Controller
public class MainPageController {

    @Autowired
    StorageController storage;

    @GetMapping("/home/{groupId}")
    String home(@PathVariable Long groupId) {
        return "redirect:-1/";
    }

    @GetMapping("/home/{groupId}/{noteId}")
    String home(
            @PathVariable(required = false) Long groupId,
            @PathVariable(required = false) Long noteId,
            Principal principal, Model model) {

        User user = storage.user.getByUsername(principal.getName());
        model.addAttribute("user", user);
        List<Group> searchGroups = storage.group.findByUserOrderByName(user);

        List<Group> groups = new ArrayList<>();
        List<Group> def = new ArrayList<>();
        List<Group> fixed = new ArrayList<>();

        for (Group group : searchGroups) {
            if (group.testFlag(Flags.ROOT)) {
                def.add(group);
            }
            else if (group.testFlag(Flags.USER)) {
                fixed.add(group);
            } else {
                groups.add(group);
            }
        }
        LinkedList<List<Group>> superGroups = new LinkedList<>();
        superGroups.add(def);
        superGroups.add(fixed);
        superGroups.add(groups);
        model.addAttribute("superGroups", superGroups);


        Group currentGroup = def.get(0);
        if (groups.size() > 0)
            currentGroup = groups.get(0);

        if (groupId != null) {
            Optional<Group> selected = storage.group.findById(groupId);
            if (selected.isPresent() && (selected.get().getUser() == user)) {
                currentGroup = selected.get();
            }
        }
        model.addAttribute("noAdd", currentGroup.testFlag(Flags.NO_ADD));


        model.addAttribute("selectedGroup", currentGroup.getId());
        TreeSet<Note> notes = new TreeSet<>(Comparator.comparing(Note::getUpdateDate).reversed());
        notes.addAll(currentGroup.getNotes());
        model.addAttribute("notes", notes);

        model.addAttribute("noteIsSelected", false);
        model.addAttribute("selectedNote", -1);

        if (noteId != null) {
            Optional<Note> selected = storage.note.findById(noteId);
            if (selected.isPresent() && (selected.get().getGroup() == currentGroup)) {
                model.addAttribute("noteIsSelected", true);
                model.addAttribute("selectedNote", noteId);
                model.addAttribute("note", new NoteView(selected.get().getName(), selected.get().getContent()));
            }
        }

        model.addAttribute("search", new SearchView());
        return "home";
    }
}
