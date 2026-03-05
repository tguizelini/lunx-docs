import { UseCase } from '../../../application/base/use-case.base';
import { TeamEntity } from './entities/team.entity';
import { ITeamsRepository } from './port/out/teams.repository';
import { ILogger } from '../../../infrastructure/logger/services/logger.interface';
export declare class TeamsGetAllUseCase implements UseCase<void, TeamEntity[]> {
    private readonly logger;
    private readonly teamsRepository;
    constructor(logger: ILogger, teamsRepository: ITeamsRepository);
    execute(): Promise<TeamEntity[]>;
}
