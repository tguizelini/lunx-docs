## Code Challenge
### Task:
Create a simple RESTful API using Node.js and Express that allows users to manage a list of tasks. Each task should have the following properties:

- id (number): Unique identifier for the task
- title (string): Title of the task
- completed (boolean): Indicates whether the task is completed
The API should support the following endpoints:

GET /tasks - Retrieve all tasks
POST /tasks - Create a new task
PUT /tasks/:id - Update an existing task by ID
DELETE /tasks/:id - Delete a task by ID

### Requirements:
- Use Node.js and Express for the backend.
- Use in-memory storage (an array) for tasks (no database required).
- Validate incoming data for the POST and PUT requests.
- Ensure that the API follows RESTful conventions.

### Constraints:
- The challenge should be solvable in less than 30 minutes.
- Ensure code quality and use ES6+ features.

### External Resources
Node.js
Express.js
Postman for testing the API (optional)