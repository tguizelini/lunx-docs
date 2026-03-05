import { HttpException, HttpStatus, Inject, Injectable } from '@nestjs/common';
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

@Injectable()
export class TeamsService implements ITeamsService {
  constructor(
    @Inject(ILogger)
    private readonly logger: ILogger,
    private readonly ucGetAll: TeamsGetAllUseCase,
    private readonly ucGetById: TeamsGetByIdUseCase,
    private readonly ucCreate: TeamsCreateUseCase,
    private readonly ucUpdate: TeamsUpdateUseCase,
    private readonly ucDelete: TeamsDeleteUseCase,
  ) {}

  create(createTeamDto: CreateTeamDto): Promise<TeamEntity> {
    try {
      return this.ucCreate.execute(createTeamDto);
    } catch (e) {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      this.logger.error('TeamsService::create', e.message);
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      throw new HttpException(e.message, HttpStatus.FORBIDDEN);
    }
  }

  findAll(): Promise<TeamEntity[]> {
    try {
      return this.ucGetAll.execute();
    } catch (e) {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      this.logger.error('TeamsService::update', e.message);
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      throw new HttpException(e.message, HttpStatus.FORBIDDEN);
    }
  }

  findOne(id: string): Promise<TeamEntity | null> {
    return this.ucGetById.execute(id);
  }

  update(id: string, updateTeamDto: UpdateTeamDto): void {
    try {
      updateTeamDto.id = id;
      this.ucUpdate.execute(updateTeamDto);
    } catch (e) {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      this.logger.error('TeamsService::update', e.message);
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      throw new HttpException(e.message, HttpStatus.FORBIDDEN);
    }
  }

  remove(id: string): void {
    try {
      this.ucDelete.execute(id);
    } catch (e) {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      this.logger.error('TeamsService::update', e.message);
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      throw new HttpException(e.message, HttpStatus.FORBIDDEN);
    }
  }
}
