package com.trekari.site.controller;

import com.trekari.site.model.User;
import com.trekari.site.repository.UserRepository;
import com.trekari.site.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    public CartController(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    /** Load cart contents (HTML fragment) */
    @GetMapping("/fragment")
    public String cartFragment(@AuthenticationPrincipal UserDetails userDetails,
                            Model model) {

        if (userDetails != null) {
            User user = userRepository.findByUsername(userDetails.getUsername());
            model.addAttribute("cartItems", cartService.getCartForUser(user).getItems()); // <--- key change
        }

        return "fragments/cart :: cart";
    }


    /** Add item via AJAX */
    @PostMapping("/add")
    public String addToCartFragment(@AuthenticationPrincipal UserDetails userDetails,
                                    @RequestParam Long id,
                                    @RequestParam(defaultValue = "1") int quantity,
                                    Model model) {

        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findByUsername(userDetails.getUsername());
        cartService.addItem(user, id, quantity);

        model.addAttribute("cartItems", cartService.getCartForUser(user).getItems());
        return "fragments/cart :: cart"; // return updated fragment
    }


    /** Remove item via AJAX */
    @PostMapping("/remove")
    @ResponseBody
    public Map<String, String> removeFromCart(@AuthenticationPrincipal UserDetails userDetails,
                                              @RequestParam Long id) {

        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findByUsername(userDetails.getUsername());
        cartService.removeItem(user, id);

        Map<String, String> resp = new HashMap<>();
        resp.put("status", "ok");
        return resp;
    }
}
