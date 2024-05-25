package com.bitscoder.onlinebookstore.controller;

import com.bitscoder.onlinebookstore.dto.ApiResponse;
import com.bitscoder.onlinebookstore.dto.SignInRequest;
import com.bitscoder.onlinebookstore.dto.UserRegistrationRequest;
import com.bitscoder.onlinebookstore.security.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
@AllArgsConstructor
@RequestMapping("/auth")
public class UserController {

    private AuthService authService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userRegistrationRequest", new UserRegistrationRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute UserRegistrationRequest request, RedirectAttributes redirectAttributes) {
        try {
            UserRegistrationRequest.Response response = authService.register(request);
            redirectAttributes.addFlashAttribute("message", response.getMessage());
            return "redirect:/auth/login";
        } catch (ResponseStatusException e) {
            redirectAttributes.addFlashAttribute("error", e.getReason());
            return "redirect:/auth/register";
        }
    }

    @GetMapping("/login")
    public String showSignInForm(Model model) {
        model.addAttribute("signInRequest", new SignInRequest());
        return "login";
    }

    @PostMapping("/login")
    public String signIn(@ModelAttribute SignInRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            ResponseEntity<ApiResponse<SignInRequest.Response>> apiResponse = authService.signIn(request);
            Cookie cookie = new Cookie("token", Objects.requireNonNull(apiResponse.getBody()).getData().getToken());
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
            return "userPage";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return "redirect:/";
    }
}