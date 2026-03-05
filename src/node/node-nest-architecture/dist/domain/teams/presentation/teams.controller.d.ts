import { CreateTeamDto } from './dto/create-team.dto';
import { UpdateTeamDto } from './dto/update-team.dto';
import { ITeamsService } from '../domain/port/in/teams.service';
export declare class TeamsController {
    private readonly teamsService;
    constructor(teamsService: ITeamsService);
    create(createTeamDto: CreateTeamDto): Promise<import("../domain/entities/team.entity").TeamEntity>;
    findAll(): Promise<import("../domain/entities/team.entity").TeamEntity[]>;
    findOne(id: string): Promise<import("../domain/entities/team.entity").TeamEntity | null>;
    update(id: string, updateTeamDto: UpdateTeamDto): void;
    remove(id: string): void;
}
