const SeedData = require("../seedData/seedData");

const productService = {
    list(search) {
        const groceryItems = SeedData.groceryProducts
            .filter(i => i.productName === search);

        const foodItems = SeedData.foodProducts
            .filter(i => i.productName === search);

        return [...groceryItems, ...foodItems];
    },

    getProduct(productId, outletId) {
        const product = SeedData.groceryProducts.find(groceryProduct => {
            return (
                groceryProduct.productId === productId && 
                groceryProduct.store && 
                groceryProduct.store.outletId === outletId &&
                    groceryProduct.availableStock > 0
            );
        });

        if (product) return product;

        const food = SeedData.foodProducts.find(food => {
            return (
                food.productId === productId &&
                food.restaurant&&
                food.restaurant.outletId === outletId
            );
        });

        return food;
    }
};

module.exports = productService;
