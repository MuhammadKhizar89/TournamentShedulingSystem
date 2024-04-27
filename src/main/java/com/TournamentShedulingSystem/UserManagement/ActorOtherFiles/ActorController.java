package com.TournamentShedulingSystem.UserManagement.ActorOtherFiles;

import com.TournamentShedulingSystem.TournamentManagement.TournamentOtherFiles.TournamentService;
import com.TournamentShedulingSystem.UserManagement.Actor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@Controller // Use @Controller instead of @RestController
public class ActorController {
    private final ActorService actorService;
    private TournamentService tournamentService;
    @Autowired
    public ActorController(ActorService actorService) {
        this.actorService = actorService;
    }
    @PostMapping("/signup1")
    public String signup1(@RequestParam String username, @RequestParam String password, @RequestParam String confirmPassword, HttpSession session,HttpServletRequest  request,Model model) {
        // Check if username already exists
        if (actorService.existsByUsername(username)) {
            model.addAttribute("error", "Username already exists");
            return "signup_Page"; // Return the signup form view with error message
        }
        // Check if password and confirm password match
        else if (!password.equals(confirmPassword)) {
            model.addAttribute("passwordMismatch", true);
            return "signup_Page"; // Return the signup form view with error message
        }
        else {
            // Proceed with signup process
            Actor actor = new Actor();
            actor.setUsername(username);
            actor.setPassword(password);

            // Save the actor
            Actor savedActor = actorService.signin(actor);

            // Set session attributes
            session.setAttribute("username", actor.getUsername());
            session.setAttribute("userId", savedActor.getId());
System.out.println(request.getSession().getAttribute("userId"));
            return "redirect:/"; // Redirect to root URL after successful signup
        }
    }
    @GetMapping("/signup")
    public String signup_Page(){
        return "signup_Page";
    }
    @GetMapping("/signin")
    public String signin_Page(){
        return "signin_Page";
    }
    @PostMapping("/signin1")
    public String signin1(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        if (actorService.exists(username, password)) {
            Long userId = actorService.getIdByUsernameAndPassword(username, password);
            if (userId != null) {
                session.setAttribute("username", username);
                session.setAttribute("userId", userId);
                return "redirect:/"; // Redirect to root URL after successful signin
            }
        }
        // User doesn't exist or invalid credentials, display error message on the sign-in form
        model.addAttribute("error", "Invalid username or password"); // Add error message to model
        return "signin_Page"; // Return the sign-in form view
    }






}

