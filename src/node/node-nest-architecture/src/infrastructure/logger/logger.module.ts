import { Module } from '@nestjs/common';
import { ILogger } from './services/logger.interface';
import { LoggerCommonService } from './services/logger-common.service';

const providers = [
  {
    provide: ILogger,
    useClass: LoggerCommonService,
  },
];

@Module({
  providers: [...providers],
  exports: [...providers],
})
export class LoggerModule {}
