import { ITeamsRepository } from '../domain/port/out/teams.repository';
import { CreateTeamDto } from '../presentation/dto/create-team.dto';
import { TeamEntity } from '../domain/entities/team.entity';
import { UpdateTeamDto } from '../presentation/dto/update-team.dto';
import { EntityManager } from 'typeorm';
import { ILogger } from '../../../infrastructure/logger/services/logger.interface';
export declare class TeamsRepository implements ITeamsRepository {
    private readonly logger;
    private readonly db;
    constructor(logger: ILogger, db: EntityManager);
    create(createTeamDto: CreateTeamDto): Promise<TeamEntity>;
    findAll(): Promise<TeamEntity[]>;
    findOne(id: string): Promise<TeamEntity | null>;
    update(id: string, updateTeamDto: UpdateTeamDto): void;
    remove(id: string): void;
}
