import { Logger, Module } from '@nestjs/common';
import { TeamsService } from './presentation/service/teams.service';
import { TeamsController } from './presentation/teams.controller';
import { TypeOrmModule } from '@nestjs/typeorm';
import { TeamEntity } from './domain/entities/team.entity';
import { ITeamsService } from './domain/port/in/teams.service';
import { TeamsGetAllUseCase } from './domain/get-all.use-case';
import { TeamsGetByIdUseCase } from './domain/get-by-id.use-case';
import { TeamsCreateUseCase } from './domain/create.use-case';
import { TeamsUpdateUseCase } from './domain/update.use-case';
import { ITeamsRepository } from './domain/port/out/teams.repository';
import { TeamsRepository } from './data/teams.repository';
import { TeamsDeleteUseCase } from './domain/delete.use-case';
import { InfrastructureModule } from '../../infrastructure/infrastructure.module';

@Module({
  imports: [InfrastructureModule, TypeOrmModule.forFeature([TeamEntity])],
  controllers: [TeamsController],
  providers: [
    TeamsGetAllUseCase,
    TeamsGetByIdUseCase,
    TeamsCreateUseCase,
    TeamsUpdateUseCase,
    TeamsDeleteUseCase,
    {
      provide: ITeamsService,
      useClass: TeamsService,
    },
    {
      provide: ITeamsRepository,
      useClass: TeamsRepository,
    },
  ],
})
export class TeamsModule {}
