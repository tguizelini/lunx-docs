const todoCreateRequest = require("./todoCreateRequest.dto");
const todoUpdateRequest = require("./todoUpdateRequest.dto");

module.exports = {
    todos: {
        create: todoCreateRequest,
        update: todoUpdateRequest,
    }
};