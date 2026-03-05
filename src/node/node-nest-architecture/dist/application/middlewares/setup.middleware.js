"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.setupMiddleware = void 0;
const swagger_middleware_1 = require("./config/swagger.middleware");
const global_pipes_middleware_1 = require("./config/global-pipes.middleware");
const http_middleware_1 = require("./config/http.middleware");
const setupMiddleware = (app) => {
    app.setGlobalPrefix('v1/pam/scaffold/');
    (0, http_middleware_1.setupHttp)(app);
    (0, global_pipes_middleware_1.setupGlobalPipes)(app);
    (0, swagger_middleware_1.setupSwagger)(app);
    return app;
};
exports.setupMiddleware = setupMiddleware;
//# sourceMappingURL=setup.middleware.js.map