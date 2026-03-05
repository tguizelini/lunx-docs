"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
var __param = (this && this.__param) || function (paramIndex, decorator) {
    return function (target, key) { decorator(target, key, paramIndex); }
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.TeamsGetByIdUseCase = void 0;
const common_1 = require("@nestjs/common");
const teams_repository_1 = require("./port/out/teams.repository");
const logger_interface_1 = require("../../../infrastructure/logger/services/logger.interface");
let TeamsGetByIdUseCase = class TeamsGetByIdUseCase {
    logger;
    teamsRepository;
    constructor(logger, teamsRepository) {
        this.logger = logger;
        this.teamsRepository = teamsRepository;
    }
    async execute(input) {
        this.logger.log('TeamsGetByIdUseCase', 'inicia as validacoes para buscar um time pelo id');
        const resp = await this.teamsRepository.findOne(input);
        if (!resp)
            throw new Error('Team não encontrado');
        return resp;
    }
};
exports.TeamsGetByIdUseCase = TeamsGetByIdUseCase;
exports.TeamsGetByIdUseCase = TeamsGetByIdUseCase = __decorate([
    __param(0, (0, common_1.Inject)(logger_interface_1.ILogger)),
    __param(1, (0, common_1.Inject)(teams_repository_1.ITeamsRepository)),
    __metadata("design:paramtypes", [Object, Object])
], TeamsGetByIdUseCase);
//# sourceMappingURL=get-by-id.use-case.js.map