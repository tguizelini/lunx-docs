const cartService = require("../services/cartService");

const cartController = {
  addProductToCart(req, res) {
    const result = cartService.addProductToCartForUser(req.body);
    res.status(200).json(result);
  },

  viewCart(req, res) {
    const cart = cartService.getCartForUser(req.query.userId);
    res.status(200).json(cart);
  },
};

module.exports = cartController;
