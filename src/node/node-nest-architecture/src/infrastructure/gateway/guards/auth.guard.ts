import { Observable } from 'rxjs';
import {
  CanActivate,
  ExecutionContext,
  Inject,
  Injectable,
} from '@nestjs/common';
import { IAuthService } from './auth-service.interface';

@Injectable()
export class AuthGuard implements CanActivate {
  constructor(
    @Inject(IAuthService)
    private readonly authService: IAuthService,
  ) {}
  canActivate(
    context: ExecutionContext,
  ): boolean | Promise<boolean> | Observable<boolean> {
    const token = '123'; //pega o token do header..
    return this.authService.validate(token);
  }
}
