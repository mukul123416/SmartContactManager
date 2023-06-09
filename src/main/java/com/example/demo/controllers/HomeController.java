package com.example.demo.controllers;

import com.example.demo.entities.Notification;
import com.example.demo.entities.User;
import com.example.demo.helper.Message;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.NotificationService;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping("/home")
    public String home(Model model){
        model.addAttribute("title","Home - Smart Contact Manager");
        return "home";
    }
    @RequestMapping("/about")
    public String about(Model model){
        model.addAttribute("title","About - Smart Contact Manager");
        return "about";
    }
    @RequestMapping("/signup")
    public String signup(Model model){
        model.addAttribute("title","Register - Smart Contact Manager");
        model.addAttribute("user",new User());
        return "signup";
    }

    @RequestMapping(value = "/do_register",method = RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, @RequestParam("profileImage") MultipartFile profileImage, @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, RedirectAttributes redirAttrs){
        try{
            if(!agreement){
                throw new Exception("You have not agreed the term and conditions");
            }
            if(profileImage.isEmpty()) {
                user.setImageUrl("default.png");
            }else{
                user.setImageUrl(profileImage.getOriginalFilename());
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+profileImage.getOriginalFilename());
                Files.copy(profileImage.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            if(bindingResult.hasErrors()){
                return "signup";
            }
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            this.userService.addUser(user);
            redirAttrs.addFlashAttribute("success", "Successfully Registered !!");
            Notification notification = new Notification();
            notification.setRead(false);
            notification.setMessage("A new user has been created.");
            notification.setUser(user);
            this.notificationService.addNotification(notification);
            return "redirect:/signup";
        }catch (Exception ex){
            redirAttrs.addFlashAttribute("error", "Something Went wrong !!" + ex.getMessage());
            return "redirect:/signup";
        }
    }
    @RequestMapping("/signin")
    public String customLogin(Model model){
        model.addAttribute("title","Login Page");
        return "login";
    }

    @RequestMapping(value = "/user/open_payments_form",method=RequestMethod.POST)
    public String openPaymentsForm(Model model, Principal principal, @RequestParam("donateName") String donateName, HttpSession session) {
        try{
            String name=principal.getName();
            User user=this.userRepository.getUserByUserName(name);
            if(user.getName().equals(donateName)){
                model.addAttribute("user",user);
                model.addAttribute("title","Payment Dashboard");
                return "normal/payments_page";
            }else {
                model.addAttribute("user",user);
                session.setAttribute("message", new Message("Something Went wrong !! Invalid name","alert-danger"));
                return "normal/user_dashboard";
            }
        }catch (Exception ex){
            String name=principal.getName();
            User user=this.userRepository.getUserByUserName(name);
            model.addAttribute("user",user);
            session.setAttribute("message", new Message("Something Went wrong !!" + ex.getMessage(),"alert-danger"));
            ex.getStackTrace();
            return "normal/user_dashboard";
        }
    }
}
