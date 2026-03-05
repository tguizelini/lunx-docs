import { ITeamsRepository } from '../domain/port/out/teams.repository';
import { CreateTeamDto } from '../presentation/dto/create-team.dto';
import { TeamEntity } from '../domain/entities/team.entity';
import { UpdateTeamDto } from '../presentation/dto/update-team.dto';
import { InjectEntityManager } from '@nestjs/typeorm';
import { EntityManager } from 'typeorm';
import { ILogger } from '../../../infrastructure/logger/services/logger.interface';
import { Inject } from '@nestjs/common';

export class TeamsRepository implements ITeamsRepository {
  constructor(
    @Inject(ILogger)
    private readonly logger: ILogger,
    @InjectEntityManager()
    private readonly db: EntityManager,
  ) {}

  create(createTeamDto: CreateTeamDto): Promise<TeamEntity> {
    return this.db.save(TeamEntity, createTeamDto);
  }

  findAll(): Promise<TeamEntity[]> {
    return this.db.find(TeamEntity);
  }

  findOne(id: string): Promise<TeamEntity | null> {
    return this.db.findOne(TeamEntity, { where: { id } });
  }

  update(id: string, updateTeamDto: UpdateTeamDto): void {
    this.logger.log(
      'TeamsRepository::update',
      `This action updates a #${id} team. ${updateTeamDto.name}`,
    );
  }

  remove(id: string): void {
    this.logger.log(
      'TeamsRepository::update',
      `This action removes a #${id} team`,
    );
    return;
  }
}
