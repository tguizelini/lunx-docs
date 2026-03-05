"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.setupHttp = void 0;
const setupHttp = (app) => {
    app.enableCors({
        origin: '*',
        methods: 'GET,HEAD,PUT,PATCH,POST,DELETE,OPTIONS',
        allowedHeaders: '*',
        credentials: false,
    });
};
exports.setupHttp = setupHttp;
//# sourceMappingURL=http.middleware.js.map