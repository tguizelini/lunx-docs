export interface IAuthService {
  validate(token: string): Promise<boolean>;
}

export const IAuthService = Symbol('IAuthService');
