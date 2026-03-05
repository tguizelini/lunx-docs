import { UseCase } from '../../../application/base/use-case.base';
import { TeamEntity } from './entities/team.entity';
import { Inject } from '@nestjs/common';
import { ITeamsRepository } from './port/out/teams.repository';
import { ILogger } from '../../../infrastructure/logger/services/logger.interface';

export class TeamsGetAllUseCase implements UseCase<void, TeamEntity[]> {
  constructor(
    @Inject(ILogger)
    private readonly logger: ILogger,
    @Inject(ITeamsRepository)
    private readonly teamsRepository: ITeamsRepository,
  ) {}

  async execute(): Promise<TeamEntity[]> {
    this.logger.log('TeamsGetAllUseCase', 'Listando todos os times');
    return this.teamsRepository.findAll();
  }
}
