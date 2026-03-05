import { IsBoolean, IsString } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class CreateTeamDto {
  @ApiProperty({ name: 'name', description: 'the name of the team' })
  @IsString()
  readonly name: string;

  @ApiProperty({
    name: 'isActive',
    description: 'TRUE - active, FALSE - inactive',
  })
  @IsBoolean()
  readonly isActive: boolean;
}
