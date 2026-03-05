import { UseCase } from '../../../application/base/use-case.base';
import { CreateTeamDto } from '../presentation/dto/create-team.dto';
import { ITeamsRepository } from './port/out/teams.repository';
import { Inject } from '@nestjs/common';
import { TeamEntity } from './entities/team.entity';
import { ILogger } from '../../../infrastructure/logger/services/logger.interface';

export class TeamsCreateUseCase implements UseCase<CreateTeamDto, TeamEntity> {
  constructor(
    @Inject(ILogger)
    private readonly logger: ILogger,
    @Inject(ITeamsRepository)
    private readonly teamsRepository: ITeamsRepository,
  ) {}

  async execute(input: CreateTeamDto): Promise<TeamEntity> {
    this.logger.log(
      'TeamsCreateUseCase',
      'inicia as validacoes para criar um novo time',
    );

    return this.teamsRepository.create(input);
  }
}
