import { CreateTeamDto } from '../../../presentation/dto/create-team.dto';
import { TeamEntity } from '../../entities/team.entity';
import { UpdateTeamDto } from '../../../presentation/dto/update-team.dto';
export interface ITeamsService {
    create(createTeamDto: CreateTeamDto): Promise<TeamEntity>;
    findAll(): Promise<TeamEntity[]>;
    findOne(id: string): Promise<TeamEntity | null>;
    update(id: string, updateTeamDto: UpdateTeamDto): void;
    remove(id: string): void;
}
export declare const ITeamsService: unique symbol;
