const express = require("express");
const todosRoutes = require("./todos.routes");

const router = express.Router();

module.exports = app => {
    app.use(express.json())

    router.get("/health", (req, res) => {
        return res.status(200).json({ status: "OK"});
    });

    router.use("/todos", todosRoutes);

    app.use("/api", router);
};