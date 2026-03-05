import { Inject } from '@nestjs/common';
import { ITeamsRepository } from './port/out/teams.repository';
import { ILogger } from '../../../infrastructure/logger/services/logger.interface';
import { UseCase } from '../../../application/base/use-case.base';

export class TeamsDeleteUseCase implements UseCase<string, void> {
  constructor(
    @Inject(ILogger)
    private readonly logger: ILogger,
    @Inject(ITeamsRepository)
    private readonly teamsRepository: ITeamsRepository,
  ) {}

  execute(input: string): Promise<void> | void {
    this.logger.log('TeamsDeleteUseCase', 'iniciando a exclusão de um time');
    this.teamsRepository.remove(input);
  }
}
