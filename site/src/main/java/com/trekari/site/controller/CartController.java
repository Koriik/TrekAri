package com.trekari.site.controller;

import com.trekari.site.model.User;
import com.trekari.site.repository.UserRepository;
import com.trekari.site.service.CartService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    public CartController(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String viewCart(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        User user = userRepository.findByUsername(userDetails.getUsername());
        model.addAttribute("cart", cartService.getCartForUser(user));
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam Long id,
                            @RequestParam(defaultValue = "1") int quantity) {
                                if (userDetails == null) {
                                    return "redirect:/login";
                                }
        User user = userRepository.findByUsername(userDetails.getUsername());
        cartService.addItem(user, id, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(@AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam Long id) {
                                    if (userDetails == null) {
                                        return "redirect:/login";
                                    }
        User user = userRepository.findByUsername(userDetails.getUsername());
        cartService.removeItem(user, id);
        return "redirect:/cart";
    }
}
