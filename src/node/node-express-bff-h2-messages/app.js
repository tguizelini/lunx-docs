const express = require('express')
const app = express()
const port = 3011

let mockData = require('./mockData')

app.use(express.json());

const removeCharacters = (value) => value.replace(/[^a-zA-Z0-9]/g, '')

app.post("/tools/messages", (req, res) => {
    const message = req.body;
    mockData.push(message)

    res.status(201).json({
        message: "Created",
        data: message
    })
})

app.get("/tools/messages/list", (req, res) => {

    console.log(`[GET]::/players/general/messages - Parameters =>`, { 
        qs: { ...req.query },
        body: { ...req.body }
    })
    
    const { 
        limit, 
        page, 
        orderBy, 
        search 
    } = req.query;

    const {
        dateStart, 
        dateEnd, 
        playerId,
        document,
        email,
        phoneNumber,
        coin,
        name,
        lastName,
        state,
        source,
        subject,
        category
    } = req.body

    let filteredData = mockData;

    if (dateStart && dateEnd) {
        filteredData = filteredData.filter(item => {
            const itemDate = new Date(item.date);
            return itemDate >= new Date(dateStart) && itemDate <= new Date(dateEnd);
        });
    }

    if (document) 
        filteredData = filteredData.filter(item => item.document === removeCharacters(document));

    if (email) 
        filteredData = filteredData.filter(item => item.email === email.trim());

    if (phoneNumber) 
        filteredData = filteredData.filter(item => item.phoneNumber === removeCharacters(phoneNumber));

    if (coin) 
        filteredData = filteredData.filter(item => item.coin === coin);

    if (name) 
        filteredData = filteredData.filter(item => item.name.toLowerCase() === name.toLowerCase());

    if (lastName) 
        filteredData = filteredData.filter(item => item.lastName.toLowerCase() === lastName.toLowerCase());

    if (playerId) 
        filteredData = filteredData.filter(item => item.playerId === playerId);

    if (source) 
        filteredData = filteredData.filter(item => item.source === source);

    if (subject) 
        filteredData = filteredData.filter(item => item.subject === subject);

    if (category) 
        filteredData = filteredData.filter(item => item.category === category);

    if (state) 
        filteredData = filteredData.filter(item => item.state === state);

    if (orderBy) 
        filteredData = filteredData.sort((a, b) => {
            if (a[orderBy] < b[orderBy]) return -1;
            if (a[orderBy] > b[orderBy]) return 1;
            return 0;
        });


    if (search) {
        filteredData = filteredData.filter(item => item.name.toLowerCase().includes(search.toLowerCase()));
        filteredData = filteredData.filter(item => item.lastName.toLowerCase().includes(search.toLowerCase()));
        filteredData = filteredData.filter(item => item.login.toLowerCase().includes(search.toLowerCase()));
        filteredData = filteredData.filter(item => item.subject.toLowerCase().includes(search.toLowerCase()));
    }

    const pageNum = page ? parseInt(page) : 1;
    const limitNum = limit ? parseInt(limit) : 10;
    const response = filteredData.slice(0, limitNum);

    return res.status(200).json({
        message: "Success",
        data: {
            limit: limitNum,
            page: pageNum,
            totalItems: response.length,
            items: response
        }
    });
})

app.listen(port, () => {
    console.log("app running on port " + port)
})