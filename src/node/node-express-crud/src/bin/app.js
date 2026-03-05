require("dotenv").config();

const express = require("express");
const routes = require("../routes/routes");
const middlewares = require("../middlewares/middlewares");

const app = express();

routes(app);
middlewares(app);

module.exports = app;