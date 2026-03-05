const service = require("../services/todos.service");
const dtos = require("../dtos");
const httpErrors = require("../utils/httpError.util");

const create = async (req, res, next) => {
    try {
        //const { completed } = req.query;
        //const { id } = req.params;
        console.info("TODOS::create - Starting to creation");
        const parsed = dtos.todos.create.parse(req.body);
        const result = await service.create(parsed);
        console.info("TODOS::create - Ending to creation");
        return res.status(201).json(result);
    } catch (e) {
        console.error("TODOS::create - Error on creating Todo. Error:", e);
        next(e);
    }
}

const list = async (req, res, next) => {
    try {
        console.info("TODOS::list - Starting to list Todos...");
        const { completed } = req.query;
        const completedFilter = completed === "true" || completed === "false" ?
                completed === "true" : undefined;

        const todos = await service.list(completedFilter);
        console.info("TODOS::list - Ending to list Todos...");
        return res.status(200).json(todos);
    } catch (e) {
        console.error("TODOS::list - Error on listing Todos. Error:", e);
        next(e);
    }
}

const find = async (req, res, next) => {
    try {
        console.info("TODOS::find - Starting to find Todo...");
        const { id } = req.params;
        const result = await service.find(id);
        console.info("TODOS::find - Ending to find Todo...");
        return res.status(200).json(result);
    } catch (e) {
        console.error("TODOS::find - Error on finding Todo. Error:", e);
        next(e);
    }
}

const update = async (req, res, next) => {
    try {
        console.info("TODOS::update - Starting to update Todo...");
        const { id } = req.params;
        const parsed = dtos.todos.update.parse(req.body);
        const todo = await service.update(id, parsed);
        console.info("TODOS::update - Ending to update Todo...");
        return res.status(200).json(todo);
    } catch (e) {
        console.error("TODOS::update - Error on updating Todo. Error:", e);
        next(e);
    }
}

const remove = async (req, res, next) => {
    try {
        console.info("TODOS::remove - Starting to delete Todo...");
        const { id } = req.params;
        await service.remove(id);
        console.info("TODOS::remove - Ending to delete Todo...");
        return res.status(204).send();
    } catch (e) {
        console.error("TODOS::remove - Error on deleting Todo. Error:", e);
        next(e);
    }
}

module.exports = { create, update, list, find, remove };