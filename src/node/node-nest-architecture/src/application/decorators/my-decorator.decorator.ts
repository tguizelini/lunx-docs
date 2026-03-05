export const MyDecoratorDecorator = (): MethodDecorator => {
  return (target, propertyKey, descriptor: PropertyDescriptor) => {
    descriptor.value = function () {
      return 'Acesso via Backoffice!';
    };

    return descriptor;
  };
};
