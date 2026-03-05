jest.mock("../../src/db/prisma", () => ({
    todo: {
        create: jest.fn(),
        findMany: jest.fn(),
        findUnique: jest.fn(),
        update: jest.fn(),
        delete: jest.fn(),
    },
}));

const prisma = require("../../src/db/prisma");
const service = require("../../src/services/todos.service");
const crypto = require("crypto");

const mockUuid = crypto.randomUUID();

const mockTodo = {
    id: mockUuid,
    title: "Test",
    completed: false
};

describe("todos.service", () => {
    beforeEach(() => {
        jest.clearAllMocks();
    })

    describe("create", () => {
        it("should create a todo", async () => {
            prisma.todo.create.mockResolvedValue(mockTodo);
            const result = await service.create({ title: "Test"});
            expect(prisma.todo.create).toHaveBeenCalledWith({
                data: { title: "Test" }
            });
            expect(result).toEqual(mockTodo);
            expect(prisma.todo.create).toHaveBeenCalledTimes(1);
        })
    });

    describe("list", () => {
       it("should list all when completed is undefined", async () => {
            prisma.todo.findMany.mockResolvedValue([{ id: mockUuid }]);
            const result = await service.list(undefined)
            expect(prisma.todo.findMany).toHaveBeenCalledWith({
               where: {},
               orderBy: { createdAt: "desc" }
            });
            expect(result).toEqual([{ id: mockUuid }]);
       });
    });

    it("should filter by completed when provided", async () => {
        prisma.todo.findMany.mockResolvedValue([{ id: mockUuid, completed: true }]);

        const result = await service.list(true);

        expect(prisma.todo.findMany).toHaveBeenCalledWith({
            where: { completed: true },
            orderBy: { createdAt: "desc" },
        });
        expect(result).toEqual([{ id: mockUuid, completed: true }]);
    });

    it("should return [] when prisma returns null", async () => {
        prisma.todo.findMany.mockResolvedValue(null);

        const result = await service.list(undefined);

        expect(result).toEqual([]);
    });

    describe("find", () => {
        it("should return todo when found", async () => {
            prisma.todo.findUnique.mockResolvedValue({ id: mockUuid });

            const result = await service.find(mockUuid);

            expect(prisma.todo.findUnique).toHaveBeenCalledWith({ where: { id: mockUuid } });
            expect(result).toEqual({ id: mockUuid });
        });

        it("should throw NOT_FOUND when not found", async () => {
            prisma.todo.findUnique.mockResolvedValue(null);

            await expect(service.find("404")).rejects.toMatchObject({
                statusCode: 404,
                code: "NOT_FOUND",
            });

            expect(prisma.todo.findUnique).toHaveBeenCalledWith({ where: { id: "404" } });
        });
    });

    describe("update", () => {
        it("should throw NOT_FOUND when todo does not exist", async () => {
            prisma.todo.findUnique.mockResolvedValue(null);

            await expect(service.update(mockUuid, { title: "X" })).rejects.toMatchObject({
                statusCode: 404,
                code: "NOT_FOUND",
            });

            expect(prisma.todo.update).not.toHaveBeenCalled();
        });

        it("should update when exists", async () => {
            prisma.todo.findUnique.mockResolvedValue({ id: mockUuid });
            prisma.todo.update.mockResolvedValue({ id: mockUuid, title: "New" });

            const result = await service.update(mockUuid, { title: "New" });

            expect(prisma.todo.update).toHaveBeenCalledWith({
                where: { id: mockUuid },
                data: { title: "New" },
            });
            expect(result).toEqual({ id: mockUuid, title: "New" });
        });
    });

    describe("remove", () => {
        it("should throw NOT_FOUND when todo does not exist", async () => {
            prisma.todo.findUnique.mockResolvedValue(null);

            await expect(service.remove("1")).rejects.toMatchObject({
                statusCode: 404,
                code: "NOT_FOUND",
            });

            expect(prisma.todo.delete).not.toHaveBeenCalled();
        });

        it("should delete when exists", async () => {
            prisma.todo.findUnique.mockResolvedValue({ id: mockUuid});
            prisma.todo.delete.mockResolvedValue({});

            await service.remove(mockUuid);

            expect(prisma.todo.delete).toHaveBeenCalledWith({ where: { id: mockUuid } });
        });
    });
});