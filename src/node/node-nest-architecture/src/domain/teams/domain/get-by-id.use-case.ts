import { Inject } from '@nestjs/common';
import { ITeamsRepository } from './port/out/teams.repository';
import { ILogger } from '../../../infrastructure/logger/services/logger.interface';
import { UseCase } from '../../../application/base/use-case.base';
import { TeamEntity } from './entities/team.entity';

export class TeamsGetByIdUseCase implements UseCase<string, TeamEntity> {
  constructor(
    @Inject(ILogger)
    private readonly logger: ILogger,
    @Inject(ITeamsRepository)
    private readonly teamsRepository: ITeamsRepository,
  ) {}

  async execute(input: string): Promise<TeamEntity> {
    this.logger.log(
      'TeamsGetByIdUseCase',
      'inicia as validacoes para buscar um time pelo id',
    );

    const resp = await this.teamsRepository.findOne(input);

    if (!resp) throw new Error('Team não encontrado');

    return resp;
  }
}
