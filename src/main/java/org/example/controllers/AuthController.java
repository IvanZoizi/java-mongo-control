package org.example.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.dto.AuthDTO;
import org.example.service.AuthService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Data
@AllArgsConstructor
public class AuthController {
    private AuthService authService;

    @PostMapping("/login")
    public String login(@RequestBody AuthDTO authDTO, HttpSession session) {

        MongoTemplate mongoTemplate = authService.login(authDTO);

        session.setAttribute("MongoTemplate", mongoTemplate);
        session.setAttribute("Login", authDTO.getLogin());
        session.setAttribute("Role", authService.getRole(authDTO.getLogin()));

        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}