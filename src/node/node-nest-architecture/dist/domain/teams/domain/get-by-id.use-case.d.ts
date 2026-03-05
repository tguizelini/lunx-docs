import { ITeamsRepository } from './port/out/teams.repository';
import { ILogger } from '../../../infrastructure/logger/services/logger.interface';
import { UseCase } from '../../../application/base/use-case.base';
import { TeamEntity } from './entities/team.entity';
export declare class TeamsGetByIdUseCase implements UseCase<string, TeamEntity> {
    private readonly logger;
    private readonly teamsRepository;
    constructor(logger: ILogger, teamsRepository: ITeamsRepository);
    execute(input: string): Promise<TeamEntity>;
}
