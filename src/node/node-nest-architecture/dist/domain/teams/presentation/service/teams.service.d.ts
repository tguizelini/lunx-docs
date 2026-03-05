import { CreateTeamDto } from '../dto/create-team.dto';
import { UpdateTeamDto } from '../dto/update-team.dto';
import { TeamEntity } from '../../domain/entities/team.entity';
import { ITeamsService } from '../../domain/port/in/teams.service';
import { ILogger } from '../../../../infrastructure/logger/services/logger.interface';
import { TeamsGetAllUseCase } from '../../domain/get-all.use-case';
import { TeamsGetByIdUseCase } from '../../domain/get-by-id.use-case';
import { TeamsCreateUseCase } from '../../domain/create.use-case';
import { TeamsUpdateUseCase } from '../../domain/update.use-case';
import { TeamsDeleteUseCase } from '../../domain/delete.use-case';
export declare class TeamsService implements ITeamsService {
    private readonly logger;
    private readonly ucGetAll;
    private readonly ucGetById;
    private readonly ucCreate;
    private readonly ucUpdate;
    private readonly ucDelete;
    constructor(logger: ILogger, ucGetAll: TeamsGetAllUseCase, ucGetById: TeamsGetByIdUseCase, ucCreate: TeamsCreateUseCase, ucUpdate: TeamsUpdateUseCase, ucDelete: TeamsDeleteUseCase);
    create(createTeamDto: CreateTeamDto): Promise<TeamEntity>;
    findAll(): Promise<TeamEntity[]>;
    findOne(id: string): Promise<TeamEntity | null>;
    update(id: string, updateTeamDto: UpdateTeamDto): void;
    remove(id: string): void;
}
