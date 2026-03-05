import express from 'express'
import bodyParser
 from 'body-parser'
const app = express()

app.use(bodyParser.urlencoded())
app.use(bodyParser.json())

let tasks: Task[] = []

app.get('/', (req, res) => {
  tasks = []
  res.status(200).send("Hello world")
})

app.get('/tasks', (req, res) => {
  res.status(200).send(JSON.stringify(tasks))
})

app.delete('/tasks/:id', (req, res) => {
  const id = req.params.id
  const task = tasks.find((i: Task) => i.id == id)

  if (!task) 
    return res.status(404).send("No task was found with ID sent")

  tasks = tasks.filter((i: Task) => i.id != id)

  res.status(200).send("removed")
})
   
app.post('/tasks', (req, res) => {
  if (Object.keys(req.body).length == 0) 
    return res.status(400).send("No Task sent")

  tasks.push({
    id: req.body.id,
    title: req.body.title,
    completed: req.body.completed
  })

  res.status(201).send("added")
});

app.put('/tasks/:id', (req, res) => {
  if (Object.keys(req.body).length == 0) 
    return res.status(400).send("No Task sent")

  const id = req.params.id
  const task = tasks.find((i: Task) => i.id == id)

  if (!task) 
    return res.status(404).send("No task was found with ID sent")

  tasks = tasks.filter((i: Task) => i.id != id)

  tasks.push({
    id: task.id,
    title: req.body.title,
    completed: req.body.completed
  })

  res.status(200).send('updated')
});

const port = parseInt(process.env.PORT || '3000')

app.listen(port, () => {
  console.log(`listening on port ${port}`)
});
