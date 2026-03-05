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
exports.TeamsService = void 0;
const common_1 = require("@nestjs/common");
const logger_interface_1 = require("../../../../infrastructure/logger/services/logger.interface");
const get_all_use_case_1 = require("../../domain/get-all.use-case");
const get_by_id_use_case_1 = require("../../domain/get-by-id.use-case");
const create_use_case_1 = require("../../domain/create.use-case");
const update_use_case_1 = require("../../domain/update.use-case");
const delete_use_case_1 = require("../../domain/delete.use-case");
let TeamsService = class TeamsService {
    logger;
    ucGetAll;
    ucGetById;
    ucCreate;
    ucUpdate;
    ucDelete;
    constructor(logger, ucGetAll, ucGetById, ucCreate, ucUpdate, ucDelete) {
        this.logger = logger;
        this.ucGetAll = ucGetAll;
        this.ucGetById = ucGetById;
        this.ucCreate = ucCreate;
        this.ucUpdate = ucUpdate;
        this.ucDelete = ucDelete;
    }
    create(createTeamDto) {
        try {
            return this.ucCreate.execute(createTeamDto);
        }
        catch (e) {
            this.logger.error('TeamsService::create', e.message);
            throw new common_1.HttpException(e.message, common_1.HttpStatus.FORBIDDEN);
        }
    }
    findAll() {
        try {
            return this.ucGetAll.execute();
        }
        catch (e) {
            this.logger.error('TeamsService::update', e.message);
            throw new common_1.HttpException(e.message, common_1.HttpStatus.FORBIDDEN);
        }
    }
    findOne(id) {
        return this.ucGetById.execute(id);
    }
    update(id, updateTeamDto) {
        try {
            updateTeamDto.id = id;
            this.ucUpdate.execute(updateTeamDto);
        }
        catch (e) {
            this.logger.error('TeamsService::update', e.message);
            throw new common_1.HttpException(e.message, common_1.HttpStatus.FORBIDDEN);
        }
    }
    remove(id) {
        try {
            this.ucDelete.execute(id);
        }
        catch (e) {
            this.logger.error('TeamsService::update', e.message);
            throw new common_1.HttpException(e.message, common_1.HttpStatus.FORBIDDEN);
        }
    }
};
exports.TeamsService = TeamsService;
exports.TeamsService = TeamsService = __decorate([
    (0, common_1.Injectable)(),
    __param(0, (0, common_1.Inject)(logger_interface_1.ILogger)),
    __metadata("design:paramtypes", [Object, get_all_use_case_1.TeamsGetAllUseCase,
        get_by_id_use_case_1.TeamsGetByIdUseCase,
        create_use_case_1.TeamsCreateUseCase,
        update_use_case_1.TeamsUpdateUseCase,
        delete_use_case_1.TeamsDeleteUseCase])
], TeamsService);
//# sourceMappingURL=teams.service.js.map