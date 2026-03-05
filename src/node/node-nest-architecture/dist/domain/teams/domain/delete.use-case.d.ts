import { ITeamsRepository } from './port/out/teams.repository';
import { ILogger } from '../../../infrastructure/logger/services/logger.interface';
import { UseCase } from '../../../application/base/use-case.base';
export declare class TeamsDeleteUseCase implements UseCase<string, void> {
    private readonly logger;
    private readonly teamsRepository;
    constructor(logger: ILogger, teamsRepository: ITeamsRepository);
    execute(input: string): Promise<void> | void;
}
