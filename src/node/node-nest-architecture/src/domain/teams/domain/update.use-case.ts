import { Inject } from '@nestjs/common';
import { ITeamsRepository } from './port/out/teams.repository';
import { ILogger } from '../../../infrastructure/logger/services/logger.interface';
import { UseCase } from '../../../application/base/use-case.base';
import { UpdateTeamDto } from '../presentation/dto/update-team.dto';

export class TeamsUpdateUseCase implements UseCase<UpdateTeamDto, void> {
  constructor(
    @Inject(ILogger)
    private readonly logger: ILogger,
    @Inject(ITeamsRepository)
    private readonly teamsRepository: ITeamsRepository,
  ) {}

  execute(input: UpdateTeamDto): Promise<void> | void {
    this.logger.log(
      'TeamsUpdateUseCase',
      'inicia as validacoes para atualizar um time',
    );

    this.teamsRepository.update(input.id, input);
  }
}
