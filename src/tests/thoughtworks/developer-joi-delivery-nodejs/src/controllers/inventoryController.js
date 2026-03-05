const inventoryController = {
  fetchStoreInventoryHealth(req, res) {
    const { storeId } = req.query;
    return res.status(200).json({});
  },
};

module.exports = inventoryController;
