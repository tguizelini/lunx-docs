const { z } = require("zod");

const todoUpdateRequest = z.object({
    title: z.string().trim().min(1).max(200).optional(),
    completed: z.boolean().optional()
}).refine((data) => Object.keys(data).length > 0, {
    message: "At leat one field must be provided",
});

module.exports = todoUpdateRequest;