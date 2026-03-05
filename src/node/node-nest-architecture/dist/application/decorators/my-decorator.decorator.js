"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.MyDecoratorDecorator = void 0;
const MyDecoratorDecorator = () => {
    return (target, propertyKey, descriptor) => {
        descriptor.value = function () {
            return 'Acesso via Backoffice!';
        };
        return descriptor;
    };
};
exports.MyDecoratorDecorator = MyDecoratorDecorator;
//# sourceMappingURL=my-decorator.decorator.js.map