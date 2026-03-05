const errorHandler = require("./errorHandler");
const swagger = require("./swagger");
module.exports = app => {
    app.use(errorHandler);
    swagger(app);
};