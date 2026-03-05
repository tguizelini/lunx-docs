import { HttpException, HttpStatus } from '@nestjs/common';

export class NotFoundException extends HttpException {
  constructor(message: string = 'Item não encontrado') {
    super(message, HttpStatus.NOT_FOUND);
  }
}
