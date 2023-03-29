package com.example.demo.controllers;

import com.example.demo.entities.Contact;
import com.example.demo.entities.User;
import com.example.demo.helper.Message;
import com.example.demo.repositories.ContactRepository;
import com.example.demo.repositories.MyOrderRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.ContactService;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.core.io.ClassPathResource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ContactService contactService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private MyOrderRepository orderRepository;

    @Autowired
    private UserService userService;

    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal){
        String email = principal.getName();
        User userByEmail = userRepository.getUserByUserName(email);
        model.addAttribute("user", userByEmail);
        model.addAttribute("title","User Dashboard");
        return "normal/user_dashboard";
    }

    @RequestMapping("/add-contact")
    public String openAddContactForm(Model model,Principal principal) {
        String userName=principal.getName();
        User user = userRepository.getUserByUserName(userName);
        model.addAttribute("user", user);
        model.addAttribute("title","Add Contact");
        model.addAttribute("contact",new Contact());
        return "normal/add_contact_form";
    }

    @RequestMapping(value = "/process-contact",method = RequestMethod.POST)
    public String processContact(@Valid @ModelAttribute("contact") Contact contact, BindingResult bindingResult, @RequestParam("profileImage") MultipartFile profileImage, Principal principal, Model model, HttpSession session){
        try {

            String name=principal.getName();
            User user=this.userRepository.getUserByUserName(name);

            if(profileImage.isEmpty()){
                contact.setImage("contact.png");
//                throw new Exception("File Can't be empty");
            }else {
                contact.setImage(profileImage.getOriginalFilename());
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+profileImage.getOriginalFilename());
                Files.copy(profileImage.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            if(bindingResult.hasErrors()){
                model.addAttribute("user",user);
                return "normal/add_contact_form";
            }
            model.addAttribute("user", user);
            contact.setUser(user);
            user.getContacts().add(contact);
            this.userRepository.save(user);
            session.setAttribute("message", new Message("Successfully Added !!","alert-success"));
            return "normal/add_contact_form";
        }catch (Exception e){
            String name=principal.getName();
            User user=this.userRepository.getUserByUserName(name);
            model.addAttribute("user",user);
            session.setAttribute("message", new Message("Something Went wrong !!" + e.getMessage(),"alert-danger"));
            e.getStackTrace();
            return "normal/add_contact_form";
        }
    }

    @RequestMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal){
        String name=principal.getName();
        User user=this.userRepository.getUserByUserName(name);

        //currentPage-page
        //contact Per page - 5
        Pageable pageable = PageRequest.of(page,2);

        Page<Contact> contactByUser = this.contactRepository.findContactByUser(user.getId(),pageable);
        if(contactByUser.getContent().size()!=0){
            model.addAttribute("contacts",contactByUser);
            model.addAttribute("currentPage",page);
            model.addAttribute("totalPages",contactByUser.getTotalPages());
        }

        model.addAttribute("user", user);
        model.addAttribute("title","User Contacts");

        return "normal/show_contacts";
    }

    @RequestMapping("/contact/{cId}")
    public String showContactDetail(@PathVariable("cId") Integer cId,Model m,Principal principal) {

        Contact singleContactById = this.contactService.getSingleContactById(cId);

        String name=principal.getName();
        User user=this.userRepository.getUserByUserName(name);
        m.addAttribute("user",user);

        if(user.getId()==singleContactById.getUser().getId()) {
            m.addAttribute("contact", singleContactById);
            m.addAttribute("title",singleContactById.getName());
        }

        return "normal/contact_detail";
    }

    @GetMapping("/delete/{cid}")
    public String deleteContact(@PathVariable("cid") Integer cId,Model model,Principal principal,HttpSession session) {

        Contact singleContactById = this.contactService.getSingleContactById(cId);

        String name=principal.getName();
        User user=this.userRepository.getUserByUserName(name);
        model.addAttribute("user",user);

        singleContactById.setUser(null);
        this.contactRepository.delete(singleContactById);

        session.setAttribute("message", new Message("Contact deleted successfully !!","alert-success"));

        return "redirect:/user/show-contacts/0";
    }

    @PostMapping("/update-contact/{cId}")
    public String updateForm(@PathVariable("cId") Integer cid,Model model,Principal principal) {

        String name=principal.getName();
        User user=this.userRepository.getUserByUserName(name);
        model.addAttribute("user",user);

        Contact contact = this.contactService.getSingleContactById(cid);
        model.addAttribute("contact", contact);

        model.addAttribute("title","Update Contact");
        return "normal/update_form";
    }


    @RequestMapping(value="/process-update",method=RequestMethod.POST)
    public String updateHandler(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,Model model,HttpSession session,Principal principal) {

        try {

            //old contact details
            Contact singleContactById = this.contactService.getSingleContactById(contact.getCId());

            if(!file.isEmpty()) {

                //delete old photo
                File deleteFile = new ClassPathResource("static/img").getFile();
                File file1=new File(deleteFile,singleContactById.getImage());
                file1.delete();

                //update new photo
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                contact.setImage(file.getOriginalFilename());

            }else {
                contact.setImage(singleContactById.getImage());
            }

            String name=principal.getName();
            User user=this.userRepository.getUserByUserName(name);
            contact.setUser(user);
            model.addAttribute("user",user);

            this.contactService.addContact(contact);

        }catch(Exception e) {
            e.printStackTrace();
        }
        session.setAttribute("message", new Message("Your contact is updated...","alert-success"));
        return "redirect:/user/contact/"+contact.getCId()+"";
    }

    @RequestMapping("/update-user/{userId}")
    public String updateUserForm(@PathVariable("userId") Integer userId,Model model,Principal principal) {
        String name=principal.getName();
        User user=this.userRepository.getUserByUserName(name);
        model.addAttribute("user",user);

        model.addAttribute("title","Update User");
        return "normal/update_User_form";
    }

    @RequestMapping(value="/process-update-user",method=RequestMethod.POST)
    public String updateUserHandler(@ModelAttribute User user,@RequestParam("profileImage") MultipartFile file,Model model,HttpSession session,Principal principal) throws IOException {

        try {
            //old user details
            User singleUserById = this.userService.getSingleUserById(user.getId());

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
            this.userService.addUser(user);
            String name=principal.getName();
            User user1=this.userRepository.getUserByUserName(name);
            model.addAttribute("user",user1);
            session.setAttribute("message", new Message("User is updated","alert-success"));
            return "redirect:/user/profile";
        }catch (Exception ex){
            String name=principal.getName();
            User user1=this.userRepository.getUserByUserName(name);
            session.setAttribute("message", new Message("Something Went wrong !!" + ex.getMessage(),"alert-danger"));
            ex.getStackTrace();
            return "redirect:/user/update-user/"+user1.getId()+"";
        }
    }

    @GetMapping("/profile")
    public String yourProfile(Model model,Principal principal) {

        String name=principal.getName();
        User user=this.userRepository.getUserByUserName(name);
        model.addAttribute("user",user);

        model.addAttribute("title","Profile Page");
        return "normal/profile";
    }

    @RequestMapping("/settings")
    public String settings(Model model,Principal principal) {

        String userName=principal.getName();

        User user = userRepository.getUserByUserName(userName);

        model.addAttribute("user", user);
        model.addAttribute("title","Settings");
        return "normal/settings";
    }

    @RequestMapping("/change_password_form")
    public String change_password(Model model,Principal principal) {

        String userName=principal.getName();

        User user = userRepository.getUserByUserName(userName);

        model.addAttribute("user", user);
        model.addAttribute("title","Settings");
        return "normal/setting_password_change";
    }


    @RequestMapping(value="/change-password",method=RequestMethod.POST)
    public String changePassword(@RequestParam("oldPassword") String oldPassword,@RequestParam("newPassword") String newPassword,Model model,HttpSession session,Principal principal) {

        String userName = principal.getName();
        User user = userRepository.getUserByUserName(userName);

        if(this.passwordEncoder.matches(oldPassword,user.getPassword())) {

            user.setPassword(this.passwordEncoder.encode(newPassword));
            this.userRepository.save(user);
            session.setAttribute("message", new Message("Your password successfully changed..","alert-success"));

        }else {
            session.setAttribute("message", new Message("Please Enter correct old password !!","alert-danger"));
            return "redirect:/user/change_password_form";
        }

        return "redirect:/user/index";
    }

    @RequestMapping("/download-content")
    public String downloadContent(Model model,Principal principal) {
        String name=principal.getName();
        User user=this.userRepository.getUserByUserName(name);
        model.addAttribute("user",user);

        model.addAttribute("title","Download Content");
        return "normal/download_content";
    }


    @RequestMapping("/open_payments_form")
    public String openPaymentsForm(Model model,Principal principal) {
        String name=principal.getName();
        User user=this.userRepository.getUserByUserName(name);
        model.addAttribute("user",user);

        model.addAttribute("title","Payment Dashboard");
        return "normal/payments_page";
    }

}
