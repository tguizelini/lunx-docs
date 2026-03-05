import { PartialType } from '@nestjs/mapped-types';
import { CreateCoffeeDto } from './create-coffee.dto';

// @ts-expect-error: TypeScript does not infer metadata for partial types with decorators.
export class UpdateCoffeeDto extends PartialType<CreateCoffeeDto> {}
