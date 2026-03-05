const inventoryController = require("./inventoryController");

describe("InventoryController", () => {
  let mockReq;
  let mockRes;

  beforeEach(() => {
    jest.clearAllMocks();

    mockReq = {
      query: {},
    };

    mockRes = {
      status: jest.fn().mockReturnThis(),
      json: jest.fn().mockReturnThis(),
    };
  });

  describe("fetchStoreInventoryHealth", () => {
    it("shouldReturnTheHealthOfTheStore", () => {
      const storeId = "store101";
      //add required mocking.

      inventoryController.fetchStoreInventoryHealth(mockReq, mockRes);

      //put meaning assertions
    });
  });
});
