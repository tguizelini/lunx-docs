import { INestApplication } from '@nestjs/common';
import { setupSwagger } from './config/swagger.middleware';
import { setupGlobalPipes } from './config/global-pipes.middleware';
import { setupHttp } from './config/http.middleware';

export const setupMiddleware: (app: INestApplication) => INestApplication = (
  app: INestApplication,
) => {
  app.setGlobalPrefix('v1/pam/scaffold/');

  setupHttp(app);
  setupGlobalPipes(app);
  setupSwagger(app);

  return app;
};
