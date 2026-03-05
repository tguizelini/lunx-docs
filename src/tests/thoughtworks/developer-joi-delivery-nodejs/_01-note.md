# New Requirements
- Dual Catalog Support
Grocery → Products with stock (inventory-driven).
Food → Menu items with price only (no stock management).

- Cart Rules
A customer can either add grocery products or food items to the cart.
The cart belongs to one outlet, and one user (one-on-one mapping).

- Order Fulfillment
Grocery order → Stock reduced from store.
Food order → No inventory reduction, just confirmation from the restaurant.

- Delivery Partner Assignment
Grocery orders → nearest store logic (assume already implemented).
Food orders → assign restaurants directly (since food is prepared fresh). Users basically choose the restaurant.

