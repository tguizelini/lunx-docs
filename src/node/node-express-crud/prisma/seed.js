require("dotenv").config();
const { prisma } = require("../src/db/prisma");

const seedData = async () => {
    await prisma.todo.deleteMany();

    await prisma.todo.createMany({
        data: [
            { title: "Create Express Project", completed: false },
            { title: "Build a REST API", completed: true },
            { title: "Write Tests", completed: false }
        ],
    });

    console.log("Data Seeded Successfully!");
}

seedData()
    .then(async () => {
        await prisma.$disconnect();
    })
    .catch(async (e) => {
       console.error("PRISMA::Error on seeding data:", e);
       await prisma.$disconnect();
       process.exit(1);
    });