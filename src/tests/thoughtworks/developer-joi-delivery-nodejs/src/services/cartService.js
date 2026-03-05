const userService = require("./userService");
const productService = require("./productService");

const cartService = {
  userCarts: new Map(),

  addProductToCartForUser(addProductRequest) {
    const user = userService.fetchUserById(addProductRequest.userId);

    const cart = this.fetchCartForUser(user);
    const product = productService.getProduct(
      addProductRequest.productId,
      addProductRequest.outletId
    );
    cart.products.push(product);
    return {
      cart: cart,
      product: product,
      sellingPrice: product.sellingPrice,
    };
  },

  getCartForUser(userId) {
    const user = userService.fetchUserById(userId);
    return this.fetchCartForUser(user);
  },

  fetchCartForUser(user) {
    return this.userCarts.get(user.userId);
  },
};

module.exports = cartService;
