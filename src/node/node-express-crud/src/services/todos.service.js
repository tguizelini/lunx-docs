const prisma = require("../db/prisma");
const httpError = require("../utils/httpError.util");

const create = async (req) => {
    const result = await prisma.todo.create({
        data: { title: req.title }
    });
    return result;
}

const list = async (completed) => {
    const where = completed === undefined ? {} : { completed };

    const todos = await prisma.todo.findMany({
        where,
        orderBy: { createdAt: "desc"}
    });

    return todos == null ? [] : todos;
}

const find = async (id) => {
    const todo = await prisma.todo.findUnique({
        where: { id }
    });

    if (!todo) throw httpError(404, "NOT_FOUND", "Todo not found");

    return todo;
}

const update = async (id, req) => {
        const existing = await prisma.todo.findUnique({
            where: { id }
        });

        if (!existing) throw httpError(404, "NOT_FOUND", "Todo not found");

        const result = await prisma.todo.update({
            where: { id },
            data: req,
        });

        return result;
}

const remove = async (id) => {
    const exists = await prisma.todo.findUnique({
        where: { id }
    });

    if (!exists) throw httpError(404, "NOT_FOUND", "Todo not found");

    await prisma.todo.delete({
        where: { id }
    });
}

module.exports = { create, update, list, find, remove };