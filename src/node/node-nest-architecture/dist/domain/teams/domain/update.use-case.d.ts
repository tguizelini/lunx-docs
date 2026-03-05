import { ITeamsRepository } from './port/out/teams.repository';
import { ILogger } from '../../../infrastructure/logger/services/logger.interface';
import { UseCase } from '../../../application/base/use-case.base';
import { UpdateTeamDto } from '../presentation/dto/update-team.dto';
export declare class TeamsUpdateUseCase implements UseCase<UpdateTeamDto, void> {
    private readonly logger;
    private readonly teamsRepository;
    constructor(logger: ILogger, teamsRepository: ITeamsRepository);
    execute(input: UpdateTeamDto): Promise<void> | void;
}
