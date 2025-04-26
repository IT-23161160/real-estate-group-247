package com.realEstate.controller;

import com.realEstate.model.Property;
import com.realEstate.model.Review;
import com.realEstate.model.User;
import com.realEstate.service.FavoriteService;
import com.realEstate.service.PropertyService;
import com.realEstate.service.ReviewService;
import com.realEstate.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/properties")
public class PropertyController {
    private final PropertyService propertyService;

    @Autowired
    public UserService userService;
    @Autowired
    public ReviewService reviewService;
    @Autowired
    public FavoriteService favoriteService;

    @Autowired
    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping("/add")
    public String showAddPropertyForm(Model model) {
        model.addAttribute("property", new Property());
        return "addProperty";
    }

    @PostMapping("/add")
    public String addProperty(@ModelAttribute Property property,
                              @RequestParam("imageFile") MultipartFile imageFile) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        if (!"SELLER".equalsIgnoreCase(user.getRole())) {
            throw new IllegalStateException("Only sellers can add properties.");
        }

        try {
            propertyService.addProperty(property, user.getUserId(), imageFile);
            return "redirect:/properties/seller";
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }

    @GetMapping("/all")
    public String getAllProperties(Model model) {
        model.addAttribute("properties", propertyService.getAllProperties());
        return "propertyList";
    }

    @GetMapping("/sorted-by-price")
    public String getPropertiesSortedByPrice(Model model) {
        model.addAttribute("properties", propertyService.getPropertiesSortedByPrice());
        return "propertyList";
    }

    @GetMapping("/{id}")
    public String getPropertyById(@PathVariable String id,
                                  @RequestParam(value = "editReviewId", required = false) String editReviewId,
                                  Model model,
                                  HttpSession session) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            User currentUser = userService.getUserByEmail(email);
            session.setAttribute("user", currentUser);
            model.addAttribute("sessionUserId", currentUser.getUserId());

            if ("BUYER".equals(currentUser.getRole())) {
                boolean isBookmarked = favoriteService.getFavoritesByUser(currentUser.getUserId()).stream()
                        .anyMatch(f -> f.getPropertyId().equals(id));
                model.addAttribute("isBookmarked", isBookmarked);
            }
        }

        propertyService.getPropertyById(id).ifPresent(property -> {
            model.addAttribute("property", property);

            User seller = userService.getUserById(property.getSellerId());
            model.addAttribute("seller", seller);

            try {
                List<Review> reviews = reviewService.getReviewsForProperty(id);
                model.addAttribute("reviews", reviews);

                User currentUser = (User) session.getAttribute("user");
                if (currentUser != null) {
                    boolean hasReviewed = reviews.stream()
                            .anyMatch(r -> r.getUserID().equals(currentUser.getUserId()));
                    model.addAttribute("canReview", !hasReviewed);

                    if (!hasReviewed) {
                        Review newReview = new Review(
                                UUID.randomUUID().toString(),
                                currentUser.getUserId(),
                                0,
                                id,
                                ""
                        );
                        model.addAttribute("newReview", newReview);
                    }
                }
                if (editReviewId != null) {
                    Review editReview = reviewService.getReviewByID(editReviewId);
                    model.addAttribute("editReview", editReview);
                }
                else  {
                    Review newReview = new Review();
                    newReview.setPropertyID(id);
                    newReview.setUserID(currentUser.getUserId());
                    model.addAttribute("newReview", newReview);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return "propertyDetails";
    }


    @GetMapping("/seller")
    public String getPropertiesBySeller(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"SELLER".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("properties", propertyService.getPropertiesBySeller(user.getUserId()));
        return "sellerProperties";
    }

    @GetMapping("/edit/{id}")
    public String showEditPropertyForm(@PathVariable String id, Model model) {
        propertyService.getPropertyById(id).ifPresent(property -> {
            model.addAttribute("property", property);
        });
        return "editProperty";
    }

    @PostMapping("/edit/{id}")
    public String updateProperty(@PathVariable String id,
                                 @ModelAttribute("property") Property property,
                                 @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                 Model model) {
        try {
            propertyService.updateProperty(id, property, imageFile);
            model.addAttribute("message", "Property updated successfully!");
            return "redirect:/properties/seller";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "editProperty";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteProperty(@PathVariable String id, Model model) {
        try {
            propertyService.deleteProperty(id);
            model.addAttribute("message", "Property deleted successfully!");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/properties/seller";
    }

}