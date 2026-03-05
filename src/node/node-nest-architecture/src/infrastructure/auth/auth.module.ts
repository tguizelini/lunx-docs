import { Module } from '@nestjs/common';
import { AuthService } from './auth.service';
import { IAuthService } from '../gateway/guards/auth-service.interface';

@Module({
  providers: [
    {
      provide: IAuthService,
      useClass: AuthService,
    },
  ],
  exports: [
    {
      provide: IAuthService,
      useClass: AuthService,
    },
  ],
})
export class AuthModule {}
