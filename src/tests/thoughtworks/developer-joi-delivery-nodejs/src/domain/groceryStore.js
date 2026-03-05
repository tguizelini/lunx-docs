const Outlet = require("./outlet");

class GroceryStore extends Outlet {
  /**
   * @param {Set<GroceryProduct>} inventory
   */

  constructor(name, description, outletId) {
    super(name, description, outletId);
    /** @type {Set<GroceryProduct>} */
    this.inventory = new Set();
  }
}

module.exports = GroceryStore;
