package id.ac.ui.cs.advprog.afk3.controller;

import id.ac.ui.cs.advprog.afk3.model.Enum.UserType;
import id.ac.ui.cs.advprog.afk3.model.User;
import id.ac.ui.cs.advprog.afk3.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    String createHTML = "userCreate";
    String listHTML = "userList";
    
    @Autowired
    private UserService userService;

    @GetMapping("/create")
    public String createUserPage(Model model){
        User user = new User();
        model.addAttribute("user", user);
        model.addAttribute("types", UserType.getAll());
        return createHTML;
    }

    @PostMapping("/create")
    public String createUserPost(@ModelAttribute("product") User user, Model model){
        userService.create(user);
        System.out.println("zczc"+user.getType());
        return "redirect:list";
    }

    @GetMapping("/list")
    public String userListPage(Model model){
        List<User> allUsers = userService.findAll();
        model.addAttribute("users", allUsers);
        return listHTML;
    }

    @GetMapping(value="/edit/{userId}")
    public String editProductPage(Model model, @PathVariable("userId") String username){
        User user = userService.findByUsername(username);
        if (user!=null){
            model.addAttribute("user", user);
            return "edituser";
        }
        return "redirect:../list";
    }

    @PostMapping("/edit")
    public String editProductPost(@ModelAttribute("user") User user, Model model){
        System.out.println(user.getUsername());
        userService.update(user.getUsername(), user);
        return "redirect:list";
    }
}
