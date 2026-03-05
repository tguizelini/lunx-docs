const { ZodError } = require("zod");

const errorHandler = (err, req, res, next) => {
    //erro de validacao do Zod
    if (err instanceof ZodError) {
        return res.status(400).json({
            error: "VALIDATION_ERROR",
            message: JSON.parse(err.message),
        });
    }

    //erros controlados/checked
    if (err && err.statusCode) {
        return res.status(err.statusCode).json({
            error: err.code || "ERROR",
            message: err.message
        });
    }

    if (err) {
        console.error("HTTP-ERROR::", err);
        return res.status(500).json({
            error: "INTERNAL_SERVER_ERROR",
            message: err
        });
    }

    next();
};

module.exports = errorHandler;