jest.mock("../../src/services/todos.service", () => ({
    create: jest.fn(),
    list: jest.fn(),
    find: jest.fn(),
    update: jest.fn(),
    remove: jest.fn(),
}));

const request = require("supertest");
const app = require("../../src/bin/app");
const service = require("../../src/services/todos.service");
const crypto = require("crypto");

describe("TodosController", () => {
    describe("Health Route", () => {
        it("when call health route should return status OK", async() => {
            const res = await request(app).get("/api/health");
            expect(res.status).toBe(200);
            expect(res.body).toEqual({ status: "OK" });
        });
    });

    describe("Create Todo", () => {
       it("create todo should return status 201 and created todo", async () => {
            const req = { title: "Test Todo" };
            const expectedResponse = { id: crypto.randomUUID(), title: "Test Todo", completed: false };

            service.create.mockReturnValue(Promise.resolve(expectedResponse));

            const res = await request(app).post("/api/todos").send(req);

            expect(res.status).toBe(201);
            expect(res.body).toEqual(expectedResponse);
            expect(service.create).toHaveBeenCalledTimes(1);
            expect(service.create).toHaveBeenCalledWith(req);
       });

       it("create todo should return status 400 when title is missing", async () => {
           const res = await request(app).post("/api/todos").send({});
           const bodyMessage = res.body.message[0].path[0];
           expect(res.status).toBe(400);
           expect(bodyMessage).toBe("title");
       })
    });
});