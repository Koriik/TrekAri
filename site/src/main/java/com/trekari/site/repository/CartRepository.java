package com.trekari.site.repository;

import com.trekari.site.model.Cart;
import com.trekari.site.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUser(User user);
}
