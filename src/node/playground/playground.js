/*
    Create an HTTP server using Node http module

    • Server listens on port 3000
    • When someone visits /time → return current server time
    • Any other route → return “Invalid route”
    • Response should be plain text
 */

//////// //////// //////// ////////
//////// http module ////////
//////// //////// //////// ////////
const http = require("http")

const server = http.createServer((req, res) => {
    if (req.url === "/time" && req.method === "GET") {
        const currentTime = new Date().toISOString()
        return res.status(200).end(currentTime)
    }

    res.status(404).end("Invalid route")
})

server.listen(3000, () => {
    console.log("Server running on port 3000")
})

//////// //////// //////// ////////
//////// com express ////////
//////// //////// //////// ////////

const express = require("express");
const app = express();

app.use(express.json());

app.get("/time", (req, res) => {
   return res.status(200).send(new Date().toISOString());
});

app.use((req, res) => {
    return res.status(404).send("Invalid route");
});

const port = 3000;
app.listen(port, () => console.log(`running on port ${port}`));