package com.example.demo.controllers;

import com.example.demo.entities.Contact;
import com.example.demo.entities.MyOrder;
import com.example.demo.entities.Notification;
import com.example.demo.entities.User;
import com.example.demo.helper.Message;
import com.example.demo.repositories.MyOrderRepository;
import com.example.demo.repositories.NotificationRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.ContactService;
import com.example.demo.services.NotificationService;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private MyOrderRepository myOrderRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal){
        String email = principal.getName();
        User userByEmail = userRepository.getUserByUserName(email);
        Pageable pageable = PageRequest.of(0,3, Sort.Direction.DESC,"createdAt");
        Page<User> users = this.userRepository.findAllByRole(pageable,"ROLE_USER");

        List<User> allUsers = this.userService.getAllUser();
        Long numberOfUser = allUsers.stream().filter(a -> a.getRole().equals("ROLE_USER")).collect(Collectors.counting());
        Long activeUser = allUsers.stream().filter(a -> a.isEnabled() && a.getRole().equals("ROLE_USER")).collect(Collectors.counting());
        Long nonActiveUser = allUsers.stream().filter(a->!a.isEnabled() && a.getRole().equals("ROLE_USER")).collect(Collectors.counting());

        List<Notification> allNotification = this.notificationRepository.findAllByIsRead(false);
        Long notificationCount = allNotification.stream().map(a -> a.getNotificationId()).collect(Collectors.counting());

        model.addAttribute("numberOfUser",numberOfUser);
        model.addAttribute("activeUser",activeUser);
        model.addAttribute("nonActiveUser",nonActiveUser);
        model.addAttribute("allNotifications",allNotification);
        model.addAttribute("notificationCount",notificationCount);

        model.addAttribute("users",users);
        model.addAttribute("user", userByEmail);
        model.addAttribute("title","Admin Dashboard");
        return "admin/admin_dashboard";
    }

    @RequestMapping("/show-users/{page}")
    public String showUsers(@PathVariable("page") Integer page, Model model, Principal principal){
        String name=principal.getName();
        User user=this.userRepository.getUserByUserName(name);

        //currentPage-page
        //contact Per page - 5
        Pageable pageable = PageRequest.of(page,2);

        Page<User> users = this.userRepository.findAllByRole(pageable,"ROLE_USER");
        List<Notification> allNotification = this.notificationRepository.findAllByIsRead(false);
        Long notificationCount = allNotification.stream().map(a -> a.getNotificationId()).collect(Collectors.counting());

        if(users.getContent().size()!=0){
            model.addAttribute("users",users);
            model.addAttribute("currentPage",page);
            model.addAttribute("totalPages",users.getTotalPages());
        }
        model.addAttribute("user", user);
        model.addAttribute("title","Users");
        model.addAttribute("allNotifications",allNotification);
        model.addAttribute("notificationCount",notificationCount);
        return "admin/show_users";
    }


    @RequestMapping("/user/{uId}")
    public String showUserDetail(@PathVariable("uId") Integer uId,Model m,Principal principal) {

        User userDetail = this.userService.getSingleUserById(uId);
        String name=principal.getName();
        User user=this.userRepository.getUserByUserName(name);
        m.addAttribute("user",user);

        List<Notification> allNotification = this.notificationRepository.findAllByIsRead(false);
        Long notificationCount = allNotification.stream().map(a -> a.getNotificationId()).collect(Collectors.counting());
        allNotification.stream()
                .filter(a -> a.getUser().getId() == uId)
                .map(b -> {
                    b.setRead(true);
                    this.notificationService.addNotification(b);
                    return b;
                }).collect(Collectors.toList());

        if(userDetail.getRole().equals("ROLE_USER")){
            m.addAttribute("userById", userDetail);
            m.addAttribute("title",userDetail.getName());
            m.addAttribute("notificationCount",notificationCount);
            m.addAttribute("allNotifications",allNotification);
        }
        return "admin/user_detail";
    }

    @GetMapping("/delete/{uid}")
    public String deleteUser(@PathVariable("uid") Integer uid, Model model, Principal principal, HttpSession session) {

        User UserById = this.userRepository.getById(uid);
        List<Contact> contacts = UserById.getContacts();

        List<Notification> allNotification = this.notificationRepository.findAllByIsRead(false);
        Long notificationCount = allNotification.stream().map(a -> a.getNotificationId()).collect(Collectors.counting());

        for(Contact contact:contacts){
            this.contactService.deleteContactById(contact.getCId());
        }

        List<MyOrder> all = myOrderRepository.findAll();
        for(MyOrder myOrder:all){
            int id = myOrder.getUser().getId();
            if(id==uid){
                this.myOrderRepository.deleteById(myOrder.getMyOrderId());
            }
        }

        this.userService.deleteUserById(uid);

        String name=principal.getName();
        User user=this.userRepository.getUserByUserName(name);
        model.addAttribute("user",user);
        model.addAttribute("allNotifications",allNotification);
        model.addAttribute("notificationCount",notificationCount);

        session.setAttribute("message", new Message("User deleted successfully !!","alert-success"));

        return "redirect:/admin/show-users/0";
    }

    @GetMapping("/profile")
    public String yourProfile(Model model,Principal principal) {

        String name=principal.getName();
        User user=this.userRepository.getUserByUserName(name);
        model.addAttribute("user",user);
        List<Notification> allNotification = this.notificationRepository.findAllByIsRead(false);
        Long notificationCount = allNotification.stream().map(a -> a.getNotificationId()).collect(Collectors.counting());

        model.addAttribute("title","Profile Page");
        model.addAttribute("allNotifications",allNotification);
        model.addAttribute("notificationCount",notificationCount);
        return "admin/profile";
    }

    @RequestMapping("/settings")
    public String settings(Model model,Principal principal) {

        String userName=principal.getName();

        User user = userRepository.getUserByUserName(userName);
        List<Notification> allNotification = this.notificationRepository.findAllByIsRead(false);
        Long notificationCount = allNotification.stream().map(a -> a.getNotificationId()).collect(Collectors.counting());

        model.addAttribute("user", user);
        model.addAttribute("title","Settings");
        model.addAttribute("allNotifications",allNotification);
        model.addAttribute("notificationCount",notificationCount);
        return "admin/settings";
    }

    @RequestMapping("/change_password_form")
    public String change_password(Model model,Principal principal) {

        String userName=principal.getName();

        User user = userRepository.getUserByUserName(userName);
        List<Notification> allNotification = this.notificationRepository.findAllByIsRead(false);
        Long notificationCount = allNotification.stream().map(a -> a.getNotificationId()).collect(Collectors.counting());

        model.addAttribute("user", user);
        model.addAttribute("title","Settings");
        model.addAttribute("allNotifications",allNotification);
        model.addAttribute("notificationCount",notificationCount);
        return "admin/setting_password_change";
    }

    @RequestMapping(value="/change-password",method= RequestMethod.POST)
    public String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, Model model, HttpSession session, Principal principal) {

        String userName = principal.getName();
        User user = userRepository.getUserByUserName(userName);
        List<Notification> allNotification = this.notificationRepository.findAllByIsRead(false);
        Long notificationCount = allNotification.stream().map(a -> a.getNotificationId()).collect(Collectors.counting());
        model.addAttribute("allNotifications",allNotification);
        model.addAttribute("notificationCount",notificationCount);

        if(this.passwordEncoder.matches(oldPassword,user.getPassword())) {

            user.setPassword(this.passwordEncoder.encode(newPassword));
            this.userRepository.save(user);
            session.setAttribute("message", new Message("Your password successfully changed..","alert-success"));

        }else {
            session.setAttribute("message", new Message("Please Enter correct old password !!","alert-danger"));
            return "redirect:/admin/change_password_form";
        }

        return "redirect:/admin/index";
    }


    @RequestMapping("/update-admin/{userId}")
    public String updateUserForm(@PathVariable("userId") Integer userId,Model model,Principal principal) {
        String name=principal.getName();
        User user=this.userRepository.getUserByUserName(name);
        model.addAttribute("user",user);
        List<Notification> allNotification = this.notificationRepository.findAllByIsRead(false);
        Long notificationCount = allNotification.stream().map(a -> a.getNotificationId()).collect(Collectors.counting());

        model.addAttribute("title","Update Admin");
        model.addAttribute("allNotifications",allNotification);
        model.addAttribute("notificationCount",notificationCount);
        return "admin/update_admin_form";
    }


    @RequestMapping(value="/process-update-admin",method=RequestMethod.POST)
    public String updateUserHandler(@ModelAttribute User user, @RequestParam("profileImage") MultipartFile file, Model model, HttpSession session, Principal principal) throws IOException {

        try {
            //old user details
            User singleUserById = this.userService.getSingleUserById(user.getId());
            List<Notification> allNotification = this.notificationRepository.findAllByIsRead(false);
            Long notificationCount = allNotification.stream().map(a -> a.getNotificationId()).collect(Collectors.counting());

            if(!file.isEmpty()) {
                //delete old photo
                File deleteFile = new ClassPathResource("static/img").getFile();
                File file1=new File(deleteFile,singleUserById.getImageUrl());
                file1.delete();

                //update new photo
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                user.setImageUrl(file.getOriginalFilename());

            }else {
                user.setImageUrl(singleUserById.getImageUrl());
            }
            user.setPassword(singleUserById.getPassword());
            user.setContacts(singleUserById.getContacts());
            user.setEmail(singleUserById.getEmail());
            user.setEnabled(singleUserById.isEnabled());
            user.setRole(singleUserById.getRole());
            user.setCreatedAt(singleUserById.getCreatedAt());
            user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            this.userService.addUser(user);
            String name=principal.getName();
            User user1=this.userRepository.getUserByUserName(name);
            model.addAttribute("user",user1);
            model.addAttribute("allNotifications",allNotification);
            model.addAttribute("notificationCount",notificationCount);
            session.setAttribute("message", new Message("Admin is updated","alert-success"));
            return "redirect:/admin/profile";
        }catch (Exception ex){
            String name=principal.getName();
            User user1=this.userRepository.getUserByUserName(name);
            session.setAttribute("message", new Message("Something Went wrong !!" + ex.getMessage(),"alert-danger"));
            ex.getStackTrace();
            return "redirect:/admin/update-admin/"+user1.getId()+"";
        }
    }

    @RequestMapping("/download-content")
    public String downloadContent(Model model,Principal principal) {
        String name=principal.getName();
        User user=this.userRepository.getUserByUserName(name);
        List<Notification> allNotification = this.notificationRepository.findAllByIsRead(false);
        Long notificationCount = allNotification.stream().map(a -> a.getNotificationId()).collect(Collectors.counting());
        model.addAttribute("user",user);
        model.addAttribute("allNotifications",allNotification);
        model.addAttribute("notificationCount",notificationCount);

        model.addAttribute("title","Download Content");
        return "admin/download_content";
    }

    @GetMapping("/show-transactions/{page}")
    public String showTransactions(@PathVariable("page") Integer page,Model model,Principal principal) {

        String name=principal.getName();
        User user=this.userRepository.getUserByUserName(name);
        List<Notification> allNotification = this.notificationRepository.findAllByIsRead(false);
        Long notificationCount = allNotification.stream().map(a -> a.getNotificationId()).collect(Collectors.counting());

        //currentPage-page
        //contact Per page - 5
        Pageable pageable = PageRequest.of(page,5);
        Page<MyOrder> allDonations = this.myOrderRepository.findAll(pageable);
        if(allDonations.getContent().size()!=0){
            model.addAttribute("donations",allDonations);
            model.addAttribute("currentPage",page);
            model.addAttribute("totalPages",allDonations.getTotalPages());
        }

        model.addAttribute("user", user);
        model.addAttribute("title","Transaction Page");
        model.addAttribute("allNotifications",allNotification);
        model.addAttribute("notificationCount",notificationCount);
        return "admin/show_transactions";
    }


    @RequestMapping("/transaction/{tId}")
    public String showUserDetail(@PathVariable("tId") Long tId,Model m,Principal principal) {
        MyOrder transactionDetail = this.myOrderRepository.getById(tId);
        String name=principal.getName();
        User user=this.userRepository.getUserByUserName(name);
        List<Notification> allNotification = this.notificationRepository.findAllByIsRead(false);
        Long notificationCount = allNotification.stream().map(a -> a.getNotificationId()).collect(Collectors.counting());
        m.addAttribute("user",user);
        m.addAttribute("transactionById", transactionDetail);
        m.addAttribute("title",transactionDetail.getUser().getName());
        m.addAttribute("allNotifications",allNotification);
        m.addAttribute("notificationCount",notificationCount);
        return "admin/transaction_detail";
    }


    @GetMapping("/transaction/delete/{tid}")
    public String deleteTransaction(@PathVariable("tid") Long tid, Model model, Principal principal, HttpSession session) {

        this.myOrderRepository.deleteById(tid);

        String name=principal.getName();
        User user=this.userRepository.getUserByUserName(name);
        List<Notification> allNotification = this.notificationRepository.findAllByIsRead(false);
        Long notificationCount = allNotification.stream().map(a -> a.getNotificationId()).collect(Collectors.counting());
        model.addAttribute("user",user);
        model.addAttribute("allNotifications",allNotification);
        model.addAttribute("notificationCount",notificationCount);

        session.setAttribute("message", new Message("Transaction deleted successfully !!","alert-success"));

        return "redirect:/admin/show-transactions/0";
    }

    @GetMapping("/user/deactivate/{uid}")
    public String deactivateAccount(@PathVariable("uid") Integer uid, Model model, Principal principal, HttpSession session) {

        User singleUserById = this.userService.getSingleUserById(uid);
        singleUserById.setEnabled(false);
        this.userService.updateUserById(singleUserById,uid);

        List<Notification> allNotification = this.notificationRepository.findAllByIsRead(false);
        Long notificationCount = allNotification.stream().map(a -> a.getNotificationId()).collect(Collectors.counting());

        String name=principal.getName();
        User user=this.userRepository.getUserByUserName(name);
        model.addAttribute("user",user);
        model.addAttribute("allNotifications",allNotification);
        model.addAttribute("notificationCount",notificationCount);

        session.setAttribute("message", new Message("Account deactivate successfully !!","alert-success"));

        return "redirect:/admin/show-users/0";
    }


    @GetMapping("/user/activate/{uid}")
    public String activateAccount(@PathVariable("uid") Integer uid, Model model, Principal principal, HttpSession session) {

        User singleUserById = this.userService.getSingleUserById(uid);
        singleUserById.setEnabled(true);
        this.userService.updateUserById(singleUserById,uid);

        List<Notification> allNotification = this.notificationRepository.findAllByIsRead(false);
        Long notificationCount = allNotification.stream().map(a -> a.getNotificationId()).collect(Collectors.counting());

        String name=principal.getName();
        User user=this.userRepository.getUserByUserName(name);
        model.addAttribute("user",user);
        model.addAttribute("allNotifications",allNotification);
        model.addAttribute("notificationCount",notificationCount);

        session.setAttribute("message", new Message("Account activate successfully !!","alert-success"));

        return "redirect:/admin/show-users/0";
    }

}
