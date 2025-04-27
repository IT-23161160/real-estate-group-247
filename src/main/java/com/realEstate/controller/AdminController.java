package com.realEstate.controller;

import com.realEstate.model.Property;
import com.realEstate.model.User;
import com.realEstate.service.PropertyService;
import com.realEstate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private PropertyService propertyService;

    public AdminController(UserService userService, PropertyService propertyService) {
        this.userService = userService;
        this.propertyService = propertyService;
    }

    @GetMapping("/users")
    public String viewAllUsers(Model model) {
        List<User> users = userService.getAllUsers();

        Map<String, Integer> propertyCounts = new HashMap<>();
        for (User user : users) {
            if (user.getRole().equals("SELLER")) {
                propertyCounts.put(user.getUserId(),
                        propertyService.getPropertiesBySeller(user.getUserId()).size());
            }
        }

        model.addAttribute("users", users);
        model.addAttribute("propertyCounts", propertyCounts);
        return "adminUsers";
    }

    @GetMapping("/properties")
    public String viewAllProperties(Model model) {
        model.addAttribute("properties", propertyService.getAllProperties());
        model.addAttribute("userService", userService);
        return "adminProperties";
    }

    @GetMapping("/users/{userId}")
    public String viewUserDetails(@PathVariable String userId, Model model) {
        User user = userService.getUserById(userId);
        List<Property> userProperties = propertyService.getPropertiesBySeller(userId);

        model.addAttribute("user", user);
        model.addAttribute("properties", userProperties);
        return "adminUserDetails";
    }

    @PostMapping("/users/delete/{userId}")
    public String deleteUser(@PathVariable String userId) {
        if ("SELLER".equals(userService.getUserById(userId).getRole())) {
            List<Property> userProperties = propertyService.getPropertiesBySeller(userId);
            for (Property property : userProperties) {
                propertyService.deleteProperty(property.getPropertyId());
            }
        }

        userService.deleteUser(userId);
        return "redirect:/admin/users";
    }

    @PostMapping("/properties/delete/{propertyId}")
    public String deleteProperty(@PathVariable String propertyId) {
        propertyService.deleteProperty(propertyId);
        return "redirect:/admin/properties";
    }

    @PostMapping("/properties/mark-sold/{propertyId}")
    public String markPropertyAsSold(@PathVariable String propertyId) {
        propertyService.markPropertyAsSold(propertyId);
        return "redirect:/admin/properties/" + propertyId;
    }
}