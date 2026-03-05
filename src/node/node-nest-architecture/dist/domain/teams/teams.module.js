"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.TeamsModule = void 0;
const common_1 = require("@nestjs/common");
const teams_service_1 = require("./presentation/service/teams.service");
const teams_controller_1 = require("./presentation/teams.controller");
const typeorm_1 = require("@nestjs/typeorm");
const team_entity_1 = require("./domain/entities/team.entity");
const teams_service_2 = require("./domain/port/in/teams.service");
const get_all_use_case_1 = require("./domain/get-all.use-case");
const get_by_id_use_case_1 = require("./domain/get-by-id.use-case");
const create_use_case_1 = require("./domain/create.use-case");
const update_use_case_1 = require("./domain/update.use-case");
const teams_repository_1 = require("./domain/port/out/teams.repository");
const teams_repository_2 = require("./data/teams.repository");
const delete_use_case_1 = require("./domain/delete.use-case");
const infrastructure_module_1 = require("../../infrastructure/infrastructure.module");
let TeamsModule = class TeamsModule {
};
exports.TeamsModule = TeamsModule;
exports.TeamsModule = TeamsModule = __decorate([
    (0, common_1.Module)({
        imports: [infrastructure_module_1.InfrastructureModule, typeorm_1.TypeOrmModule.forFeature([team_entity_1.TeamEntity])],
        controllers: [teams_controller_1.TeamsController],
        providers: [
            get_all_use_case_1.TeamsGetAllUseCase,
            get_by_id_use_case_1.TeamsGetByIdUseCase,
            create_use_case_1.TeamsCreateUseCase,
            update_use_case_1.TeamsUpdateUseCase,
            delete_use_case_1.TeamsDeleteUseCase,
            {
                provide: teams_service_2.ITeamsService,
                useClass: teams_service_1.TeamsService,
            },
            {
                provide: teams_repository_1.ITeamsRepository,
                useClass: teams_repository_2.TeamsRepository,
            },
        ],
    })
], TeamsModule);
//# sourceMappingURL=teams.module.js.map