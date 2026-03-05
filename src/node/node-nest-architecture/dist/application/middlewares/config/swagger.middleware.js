"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.setupSwagger = void 0;
const swagger_1 = require("@nestjs/swagger");
const setupSwagger = (app) => {
    const config = new swagger_1.DocumentBuilder()
        .setTitle('Scaffold')
        .setDescription('The "scaffold" API description')
        .setVersion('1.0')
        .build();
    const documentFactory = () => swagger_1.SwaggerModule.createDocument(app, config);
    swagger_1.SwaggerModule.setup('/v1/pam/scaffold/doc', app, documentFactory);
};
exports.setupSwagger = setupSwagger;
//# sourceMappingURL=swagger.middleware.js.map