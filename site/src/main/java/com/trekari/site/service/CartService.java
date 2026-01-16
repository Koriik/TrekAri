package com.trekari.site.service;

import com.trekari.site.model.Cart;
import com.trekari.site.model.CartItem;
import com.trekari.site.model.Equipment;
import com.trekari.site.model.User;
import com.trekari.site.repository.CartItemRepository;
import com.trekari.site.repository.CartRepository;
import com.trekari.site.repository.EquipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepo;
    private final CartItemRepository itemRepo;
    private final EquipmentRepository equipmentRepo;

    public CartService(CartRepository cartRepo, CartItemRepository itemRepo, EquipmentRepository equipmentRepo) {
        this.cartRepo = cartRepo;
        this.itemRepo = itemRepo;
        this.equipmentRepo = equipmentRepo;
    }

    public Cart getCartForUser(User user) {
        Cart cart = cartRepo.findByUser(user);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cartRepo.save(cart);
        }
        return cart;
    }

    public void addItem(User user, Long equipmentId, int quantity) {
        if (equipmentId == null) throw new IllegalArgumentException("equipmentId cannot be null");
        Cart cart = getCartForUser(user);
        Equipment eq = equipmentRepo.findById(equipmentId).orElseThrow();
        // check if item already exists in cart
        CartItem existing = cart.getItems().stream()
                .filter(i -> i.getEquipment().getId().equals(equipmentId))
                .findFirst().orElse(null);
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setEquipment(eq);
            item.setQuantity(quantity);
            cart.getItems().add(item);
        }
        cartRepo.save(cart);
    }

    @Transactional
    public void removeItem(User user, Long cartItemId) {
        Cart cart = getCartForUser(user);
        CartItem itemToRemove = cart.getItems().stream()
                                    .filter(i -> i.getId().equals(cartItemId))
                                    .findFirst()
                                    .orElse(null);
        if (itemToRemove != null) {
            cart.getItems().remove(itemToRemove);
            itemRepo.delete(itemToRemove); // ensure it is removed from DB
        }
    }

}
