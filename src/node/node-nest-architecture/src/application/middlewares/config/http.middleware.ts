import { INestApplication } from '@nestjs/common';

export const setupHttp = (app: INestApplication) => {
  app.enableCors({
    origin: '*',
    methods: 'GET,HEAD,PUT,PATCH,POST,DELETE,OPTIONS',
    allowedHeaders: '*',
    credentials: false,
  });
};
