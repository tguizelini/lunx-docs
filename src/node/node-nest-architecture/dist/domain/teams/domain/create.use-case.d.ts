import { UseCase } from '../../../application/base/use-case.base';
import { CreateTeamDto } from '../presentation/dto/create-team.dto';
import { ITeamsRepository } from './port/out/teams.repository';
import { TeamEntity } from './entities/team.entity';
import { ILogger } from '../../../infrastructure/logger/services/logger.interface';
export declare class TeamsCreateUseCase implements UseCase<CreateTeamDto, TeamEntity> {
    private readonly logger;
    private readonly teamsRepository;
    constructor(logger: ILogger, teamsRepository: ITeamsRepository);
    execute(input: CreateTeamDto): Promise<TeamEntity>;
}
