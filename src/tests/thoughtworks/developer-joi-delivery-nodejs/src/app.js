const express = require("express");
const cartController = require("./controllers/cartController");
const inventoryController = require("./controllers/inventoryController");
const cartService = require("./services/cartService");
const SeedData = require("./seedData/seedData");

const app = express();
app.use(express.json());

// Initialize cart service with seed data
SeedData.cartForUsers.forEach((cart, userId) => {
  cartService.userCarts.set(userId, cart);
});

// List: feed of grocery (items/products) // not impl
//


app.use((req, res, next) => {
  console.log(`${req.method} ${req.path}`);
  next();
});

app.get("/grocery", (req, res) => {
  return res.status(200).json({
    data: []
  });
});

app.post("/cart/product", (req, res) => {
  console.log("POST /cart/product route hit");
  cartController.addProductToCart(req, res);
});

app.get("/cart/view", (req, res) => {
  console.log("GET /cart/view route hit");
  cartController.viewCart(req, res);
});

app.get("/inventory/health", (req, res) => {
  console.log("GET /inventory/health route hit");
  inventoryController.fetchStoreInventoryHealth(req, res);
});

app.get("/health", (req, res) => {
  return res.status(200).json({ status: "OK" });
});

const port = process.env.PORT || 8080;
app.listen(port, () => {
  console.log(`🚀 app listening on port ${port}`);
});
