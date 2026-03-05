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
exports.TeamsCreateUseCase = void 0;
const teams_repository_1 = require("./port/out/teams.repository");
const common_1 = require("@nestjs/common");
const logger_interface_1 = require("../../../infrastructure/logger/services/logger.interface");
let TeamsCreateUseCase = class TeamsCreateUseCase {
    logger;
    teamsRepository;
    constructor(logger, teamsRepository) {
        this.logger = logger;
        this.teamsRepository = teamsRepository;
    }
    async execute(input) {
        this.logger.log('TeamsCreateUseCase', 'inicia as validacoes para criar um novo time');
        return this.teamsRepository.create(input);
    }
};
exports.TeamsCreateUseCase = TeamsCreateUseCase;
exports.TeamsCreateUseCase = TeamsCreateUseCase = __decorate([
    __param(0, (0, common_1.Inject)(logger_interface_1.ILogger)),
    __param(1, (0, common_1.Inject)(teams_repository_1.ITeamsRepository)),
    __metadata("design:paramtypes", [Object, Object])
], TeamsCreateUseCase);
//# sourceMappingURL=create.use-case.js.map