document.addEventListener("DOMContentLoaded", () => {
    // Grab CSRF token and header from meta tags
    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;

    async function updateCartFragment() {
        try {
            const res = await fetch("/cart/fragment", {
                headers: { "X-Requested-With": "XMLHttpRequest" }
            });

            if (res.status === 401) {
                document.querySelector("#sideCart").innerHTML =
                    "<p class='cart-empty'>Login to view cart</p>";
                return;
            }

            if (!res.ok) throw new Error("Failed to fetch cart fragment");

            const html = await res.text();
            document.querySelector("#sideCart").innerHTML = html;

        } catch (err) {
            console.error(err);
        }
    }


    // Immediately fetch cart on page load
    updateCartFragment(); // <-- ADD THIS

    // Add item to cart
    async function addToCart(productId, quantity = 1) {
        try {
            const res = await fetch(`/cart/add?id=${productId}&quantity=${quantity}`, {
                method: "POST",
                headers: {
                    "X-Requested-With": "XMLHttpRequest",
                    [header]: token
                }
            });

            if (res.status === 401) {
                // User not logged in → redirect to login page
                window.location.href = "/login";
                return;
            }

            if (!res.ok) throw new Error("Failed to add item");
                await updateCartFragment();
            } catch (err) {
                console.error(err);
            }
    }

    // Remove item from cart
    async function removeFromCart(cartItemId) {
        try {
            const res = await fetch(`/cart/remove?id=${cartItemId}`, {
                method: "POST",
                headers: {
                    "X-Requested-With": "XMLHttpRequest",
                    [header]: token
                }
            });
            if (!res.ok) throw new Error("Failed to remove item");
            await updateCartFragment();
        } catch (err) {
            console.error(err);
        }
    }

    // Attach add-to-cart buttons
    document.querySelectorAll(".add-to-cart-btn").forEach(btn => {
        btn.addEventListener("click", e => {
            const card = e.target.closest(".flip-card-back");
            const productId = card.dataset.id;
            const quantity = parseInt(card.querySelector("input[type=number]").value || 1);
            addToCart(productId, quantity);
        });
    });

    // Delegate remove button clicks (works for dynamically loaded cart items)
    document.querySelector("#sideCart").addEventListener("click", e => {
        if (e.target.classList.contains("remove-btn")) {
            const cartItemId = e.target.dataset.id;
            removeFromCart(cartItemId);
        }
    });

    // Optional: toggle side cart visibility
    document.getElementById("cartBtn").addEventListener("click", () => {
        document.getElementById("sideCart").classList.add("open");
        document.getElementById("cartOverlay").classList.add("active");
    });

    document.getElementById("closeCart").addEventListener("click", () => {
        document.getElementById("sideCart").classList.remove("open");
        document.getElementById("cartOverlay").classList.remove("active");
    });
});
