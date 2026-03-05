const { Product } = require("./product");

class FoodProduct extends Product {
    /**
     * @param {number} sellingPrice
     * @param {number} weight
     * @param {string} expiryDate
     * @param {number} threshold
     * @param {number} availableStock
     * @param {Restaurant} restaurant
     * @param {number} [discount]
     */

    constructor(
        productId,
        productName,
        mrp,
        sellingPrice,
        weight,
        expiryDate,
        threshold,
        availableStock,
        restaurant,
        discount = 0
    ) {
        super(productId, productName, mrp);
        /** @type {number} */
        this.sellingPrice = sellingPrice;
        /** @type {number} */
        this.weight = weight;
        /** @type {string} */
        this.expiryDate = expiryDate;
        /** @type {number} */
        this.threshold = threshold;
        /** @type {number} */
        this.availableStock = availableStock;
        /** @type {Restaurant} */
        this.restaurant = restaurant;
        /** @type {number} */
        this.discount = discount;
    }
}

module.exports = FoodProduct;
