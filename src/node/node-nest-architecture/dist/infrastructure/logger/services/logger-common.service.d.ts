import { ILogger } from './logger.interface';
import { Logger } from '@nestjs/common';
export declare class LoggerCommonService extends Logger implements ILogger {
    debug(context: string, message: string): void;
    log(context: string, message: string): void;
    error(context: string, message: string, trace?: string): void;
    warn(context: string, message: string): void;
    verbose(context: string, message: string): void;
}
