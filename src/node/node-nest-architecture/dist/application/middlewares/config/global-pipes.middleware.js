"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.setupGlobalPipes = void 0;
const common_1 = require("@nestjs/common");
const setupGlobalPipes = (app) => {
    app.useGlobalPipes(new common_1.ValidationPipe({
        whitelist: true,
        forbidNonWhitelisted: true,
    }));
};
exports.setupGlobalPipes = setupGlobalPipes;
//# sourceMappingURL=global-pipes.middleware.js.map