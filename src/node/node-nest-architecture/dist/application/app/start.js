"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.startApp = void 0;
const core_1 = require("@nestjs/core");
const app_module_1 = require("../../app.module");
const setup_middleware_1 = require("../middlewares/setup.middleware");
const microservices_1 = require("@nestjs/microservices");
const startApp = async () => {
    let app = await core_1.NestFactory.create(app_module_1.AppModule);
    app.connectMicroservice({
        transport: microservices_1.Transport.TCP,
        options: { retryAttempts: 5, retryDelay: 3000 },
    });
    app = (0, setup_middleware_1.setupMiddleware)(app);
    await app.listen(process.env.PORT ?? 3024);
    console.log('Application running on port 3013');
    console.log('Swagger at /v1/pam/scaffold/doc');
};
exports.startApp = startApp;
//# sourceMappingURL=start.js.map