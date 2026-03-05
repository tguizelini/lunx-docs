import { Module } from '@nestjs/common';
import { DatabaseModule } from './database/database.module';
import { LoggerModule } from './logger/logger.module';
import { AuthModule } from './auth/auth.module';
import { ConfigModule } from './config/config.module';

@Module({
  imports: [ConfigModule, AuthModule, DatabaseModule, LoggerModule],
  exports: [ConfigModule, AuthModule, DatabaseModule, LoggerModule],
})
export class InfrastructureModule {}
