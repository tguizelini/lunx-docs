import { NestFactory } from '@nestjs/core';
import { AppModule } from '../../app.module';
import { setupMiddleware } from '../middlewares/setup.middleware';
import { MicroserviceOptions, Transport } from '@nestjs/microservices';

export const startApp = async () => {
  let app = await NestFactory.create(AppModule);

  app.connectMicroservice<MicroserviceOptions>({
    transport: Transport.TCP,
    options: { retryAttempts: 5, retryDelay: 3000 },
  });

  app = setupMiddleware(app);

  await app.listen(process.env.PORT ?? 3024);

  console.log('Application running on port 3013');
  console.log('Swagger at /v1/pam/scaffold/doc');
};
