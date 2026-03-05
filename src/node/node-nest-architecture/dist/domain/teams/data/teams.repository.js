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
exports.TeamsRepository = void 0;
const team_entity_1 = require("../domain/entities/team.entity");
const typeorm_1 = require("@nestjs/typeorm");
const typeorm_2 = require("typeorm");
const logger_interface_1 = require("../../../infrastructure/logger/services/logger.interface");
const common_1 = require("@nestjs/common");
let TeamsRepository = class TeamsRepository {
    logger;
    db;
    constructor(logger, db) {
        this.logger = logger;
        this.db = db;
    }
    create(createTeamDto) {
        return this.db.save(team_entity_1.TeamEntity, createTeamDto);
    }
    findAll() {
        return this.db.find(team_entity_1.TeamEntity);
    }
    findOne(id) {
        return this.db.findOne(team_entity_1.TeamEntity, { where: { id } });
    }
    update(id, updateTeamDto) {
        this.logger.log('TeamsRepository::update', `This action updates a #${id} team. ${updateTeamDto.name}`);
    }
    remove(id) {
        this.logger.log('TeamsRepository::update', `This action removes a #${id} team`);
        return;
    }
};
exports.TeamsRepository = TeamsRepository;
exports.TeamsRepository = TeamsRepository = __decorate([
    __param(0, (0, common_1.Inject)(logger_interface_1.ILogger)),
    __param(1, (0, typeorm_1.InjectEntityManager)()),
    __metadata("design:paramtypes", [Object, typeorm_2.EntityManager])
], TeamsRepository);
//# sourceMappingURL=teams.repository.js.map