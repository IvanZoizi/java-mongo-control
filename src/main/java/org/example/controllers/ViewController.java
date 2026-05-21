package org.example.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@AllArgsConstructor
public class ViewController {

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        if (session.getAttribute("Login") != null) {
            model.addAttribute("username", session.getAttribute("Login"));
            model.addAttribute("role", session.getAttribute("Role"));
            return "dashboard";
        }
        return "login";
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("Login") != null) {
            return "redirect:/";
        }
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (session.getAttribute("Login") == null) {
            return "redirect:/login";
        }
        model.addAttribute("username", session.getAttribute("Login"));
        model.addAttribute("role", session.getAttribute("Role"));
        return "dashboard";
    }

    @GetMapping("/products")
    public String productsPage(HttpSession session, Model model) {
        if (session.getAttribute("Login") == null) {
            return "redirect:/login";
        }
        model.addAttribute("username", session.getAttribute("Login"));
        model.addAttribute("role", session.getAttribute("Role"));
        return "products";
    }

    @GetMapping("/orders")
    public String ordersPage(HttpSession session, Model model) {
        if (session.getAttribute("Login") == null) {
            return "redirect:/login";
        }
        model.addAttribute("username", session.getAttribute("Login"));
        model.addAttribute("role", session.getAttribute("Role"));
        return "orders";
    }

    @GetMapping("/reports")
    public String reportsPage(HttpSession session, Model model) {
        if (session.getAttribute("Login") == null) {
            return "redirect:/login";
        }
        model.addAttribute("username", session.getAttribute("Login"));
        model.addAttribute("role", session.getAttribute("Role"));
        return "reports";
    }
}