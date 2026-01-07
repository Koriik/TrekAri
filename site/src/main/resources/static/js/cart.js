// Open/close sidebar
const cartBtn = document.getElementById("cartBtn");
const sideCart = document.getElementById("sideCart");
const closeCart = document.getElementById("closeCart");
const overlay = document.getElementById("cartOverlay");

cartBtn.onclick = () => {
    sideCart.classList.add("open");
    overlay.classList.add("show");
};

closeCart.onclick = overlay.onclick = () => {
    sideCart.classList.remove("open");
    overlay.classList.remove("show");
};

// Fetch updated cart fragment and update sidebar
async function refreshCart() {
    const res = await fetch("/cart/fragment");
    const html = await res.text();
    document.querySelector("#sideCart .cart-items").innerHTML = html;
    bindRemoveButtons(); // rebind after fragment refresh
}

// Add item to cart
async function addToCart(productId, quantity = 1) {
    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;

    const res = await fetch(`/cart/add?id=${productId}&quantity=${quantity}`, {
        method: "POST",
        headers: {
            "X-Requested-With": "XMLHttpRequest",
            [header]: token
        }
    });

    if (res.status === 401) {
        window.location.href = "/login";
        return;
    }

    const html = await res.text();
    document.querySelector("#sideCart .cart-items").innerHTML = html;
    bindRemoveButtons();
}

// Remove item from cart
async function removeFromCart(productId) {
    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;

    const res = await fetch(`/cart/remove?id=${productId}`, {
        method: "POST",
        headers: {
            "X-Requested-With": "XMLHttpRequest",
            [header]: token
        }
    });

    if (res.ok) {
        await refreshCart();
    } else {
        console.error("Failed to remove item from cart");
    }
}

// Bind remove buttons dynamically
function bindRemoveButtons() {
    document.querySelectorAll(".remove-btn").forEach(btn => {
        btn.onclick = () => {
            const productId = btn.dataset.id;
            removeFromCart(productId);
        };
    });
}

// Bind add-to-cart buttons on page load
document.querySelectorAll(".add-to-cart-btn").forEach(btn => {
    btn.onclick = () => {
        const card = btn.closest(".flip-card-back");
        const productId = parseInt(card.dataset.id); // make sure it's a number
        const qty = parseInt(card.querySelector("input").value);
        addToCart(productId, qty);
    };
});

// Initial bind for remove buttons
bindRemoveButtons();
