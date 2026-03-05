class Product {
  /**
   * @param {string} productId
   * @param {string} productName
   * @param {number} mrp
   */
  constructor(productId, productName, mrp) {
    /** @type {string} */
    this.productId = productId;

    /** @type {string} */
    this.productName = productName;

    /** @type {number} */
    this.mrp = mrp;

    this.sellingPrice
    this.stock
  }
}

module.exports = Product;
