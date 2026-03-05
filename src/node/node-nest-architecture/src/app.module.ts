import { Module } from '@nestjs/common';
import { TeamsModule } from './domain/teams/teams.module';
import { InfrastructureModule } from './infrastructure/infrastructure.module';

@Module({
  imports: [InfrastructureModule, TeamsModule],
  controllers: [],
  providers: [],
})
export class AppModule {}
