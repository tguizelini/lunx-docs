import { Observable } from 'rxjs';
import { CanActivate, ExecutionContext } from '@nestjs/common';
import { IAuthService } from './auth-service.interface';
export declare class AuthGuard implements CanActivate {
    private readonly authService;
    constructor(authService: IAuthService);
    canActivate(context: ExecutionContext): boolean | Promise<boolean> | Observable<boolean>;
}
