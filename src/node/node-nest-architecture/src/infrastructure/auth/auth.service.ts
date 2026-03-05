import { Injectable } from '@nestjs/common';
import { IAuthService } from '../gateway/guards/auth-service.interface';

@Injectable()
export class AuthService implements IAuthService {
  async validate(token: string): Promise<boolean> {
    console.log('faz a validação do token');
    return '123' === token;
  }
}
