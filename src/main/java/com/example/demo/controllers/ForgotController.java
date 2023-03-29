package com.example.demo.controllers;

import com.example.demo.entities.User;
import com.example.demo.helper.Message;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Random;

@Controller
public class ForgotController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;
    Random random=new Random();


    @RequestMapping("/forgot")
    public String openEmailForm(){
        return "forgot_email_form";
    }


    @RequestMapping(value="/send-otp",method= RequestMethod.POST)
    public String sendOTP(@RequestParam("email") String email, HttpSession session){
        //generating otp
        int otp = random.nextInt(999999);
        //write code for send otp to email
        String subject="OTP From SCM";
        String message=""
                + "<div style='border:1px solid #e2e2e2; padding:20px'>"
                + "<h1>"
                + "OTP is "
                + "<b>"+otp
                + "</n>"
                + "</h1>"
                + "</div>";
        String to=email;
        boolean flag = this.emailService.sendEmail(subject, message, to);
        if(flag) {
            session.setAttribute("message", new Message("We have send OTP to your email..","alert-success"));
            session.setAttribute("OTP",otp);
            session.setAttribute("Email",email);
            return "verify_otp";
        }else {
            session.setAttribute("message", new Message("Check your email id !!","alert-danger"));
            return "forgot_email_form";
        }
    }


    @RequestMapping(value="/verify-otp",method= RequestMethod.POST)
    public String verifyOtp(@RequestParam("otp") int otp,HttpSession session) {

        int myOtp=(int) session.getAttribute("OTP");
        String email=(String) session.getAttribute("Email");

        if(myOtp==otp) {

            User user = this.userRepository.getUserByUserName(email);

            if(user==null) {
                session.setAttribute("message", new Message("User does't exists with this email !!","alert-danger"));
                return "forgot_email_form";
            }

            return "password_change_form";

        }else {
            session.setAttribute("message", new Message("You have entered wrong  otp !!","alert-danger"));
            return "verify_otp";
        }
    }


    @RequestMapping(value="/change-password",method= RequestMethod.POST)
    public String changePassword(@RequestParam("newpassword") String newpassword,HttpSession session) {
        String email=(String) session.getAttribute("Email");
        User user = this.userRepository.getUserByUserName(email);
        user.setPassword(passwordEncoder.encode(newpassword));
        this.userRepository.save(user);
        return "redirect:/signin?change=password changed successfully..";

    }

}
