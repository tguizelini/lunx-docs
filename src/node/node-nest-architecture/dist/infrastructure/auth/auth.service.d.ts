import { IAuthService } from '../gateway/guards/auth-service.interface';
export declare class AuthService implements IAuthService {
    validate(token: string): Promise<boolean>;
}
