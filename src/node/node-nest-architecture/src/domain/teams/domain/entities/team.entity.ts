import { Column, Entity, PrimaryGeneratedColumn } from 'typeorm';

@Entity('teams', { schema: 'scaffold' })
export class TeamEntity {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column()
  name: string;

  @Column('boolean', {
    name: 'is_active',
    nullable: false,
    default: () => 'true',
  })
  isActive: boolean;
}
