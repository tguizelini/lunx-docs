export interface IAuthService {
    validate(token: string): Promise<boolean>;
}
export declare const IAuthService: unique symbol;
