package com.realEstate.controller;

import com.realEstate.model.Property;
import com.realEstate.model.Transaction;
import com.realEstate.model.User;
import com.realEstate.service.PropertyService;
import com.realEstate.service.TransactionService;
import com.realEstate.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    private PropertyService propertyService;
    @Autowired
    private UserService userService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


    @GetMapping("/buy/{propertyId}")
    public String showBuyForm(@PathVariable String propertyId,
                              HttpSession session,
                              Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }
        if (!"BUYER".equals(user.getRole())) {
            return "redirect:/properties/" + propertyId + "?error=unauthorized";
        }
        Optional<Property> propertyOpt = propertyService.getPropertyById(propertyId);
        if (!propertyOpt.isPresent()) {
            return "redirect:/properties/all?error=notfound";
        }
        Property property = propertyOpt.get();
        User seller = userService.getUserById(property.getSellerId());

        model.addAttribute("propertyId", propertyId);
        model.addAttribute("price", property.getPrice());
        model.addAttribute("buyerId", user.getUserId());
        model.addAttribute("sellerId", seller.getUserId());

        return "paymentForm";
    }

    @PostMapping("/save")
    public String saveTransaction(@ModelAttribute Transaction transaction,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        try {
            transaction.setTime(LocalDateTime.now());

            Transaction savedTransaction = transactionService.addTransaction(transaction);
            propertyService.markPropertyAsSold(transaction.getPropertyId());
            redirectAttributes.addFlashAttribute("success", "Payment successful! Transaction ID: " + savedTransaction.getTransactionId());
            return "redirect:/properties/" + transaction.getPropertyId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Payment failed: " + e.getMessage());
            return "redirect:/transactions/buy/" + transaction.getPropertyId();
        }
    }

    @GetMapping("/history")
    public String viewTransactionHistory(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        if ("BUYER".equals(user.getRole())) {
            List<Transaction> purchases = transactionService.getUserTransactions(user.getUserId());
            model.addAttribute("transactions", purchases);
            model.addAttribute("historyType", "Purchase History");
        } else if ("SELLER".equals(user.getRole())) {
            List<Transaction> sales = transactionService.getUserTransactions(user.getUserId());
            model.addAttribute("transactions", sales);
            model.addAttribute("historyType", "Sales History");
        }
        return "transactionHistory";
    }

    @PostMapping("/update")
    public String updateTransaction(@ModelAttribute Transaction transaction) {
        transactionService.updateTransaction(transaction);
        return "redirect:/transactions/all";
    }

}