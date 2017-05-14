/**
 * Created by Pastuh on 02.05.2017.
 */
var express = require('express'),
    app = express(),
    bodyParser = require('body-parser'),
    mongoose = require('mongoose'),
    controller_users = require('./libs/javascript/controller_users'),
    controller_entry = require('./libs/javascript/controller_entry'),
    controller_norm = require('./libs/javascript/controller_norms'),
    controller_tutorial = require('./libs/javascript/controller_tutorial'),
    port = 3000;

app.use(bodyParser.json());

//Check DB Connection
mongoose.connect('mongodb://localhost/db');
var db = mongoose.connection;
db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function() {
    console.log('DB connected');
});
//Test
app.get('/', function (req, res) {
    res.send('Hallo, suchki!!!');
});

//////////////////////////Entries
app.get('/entries', function (req, res) {
    controller_entry.getAllEntries(req, res);
});

app.get('/entries/:id', function (req, res) {
    controller_entry.getEntryById(req, res);
});

app.post('/entries', function (req, res) {
    controller_entry.addEntry(req, res);
});

app.put('/entries/:id', function (req, res) {
    controller_entry.updateEntry(req, res);
});

app.delete('/entries/:id', function (req, res) {
    controller_entry.deleteEntry(req, res);
});
//////////////////////////Entries

app.post('/norms', function (req, res) {
    controller_norm.addNorm(req, res);
});

app.get('/norms/:id', function (req, res) {
    controller_norm.getNormById(req, res);
});

/*app.get('/entries/:id/tutorials', function (req, res) {
    controller_entry.getEntryTutorial(req, res);
});*/

app.get('/entries/:id/tutorials/:id', function (req, res) {
    controller_tutorial.getTutorialById(req, res);
});

//Debugging!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
app.get('/norms', function (req, res) {
    controller_norm.getAllNorms(req, res);
});

app.get('/tutorials', function (req, res) {
    controller_tutorial.getAllTutorials(req, res);
});

//Debugging!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


/*app.post('/users', function (req, res) {
    controller_users.addUser(req, res);
});*/

app.listen(port, function () {
    console.log('Server online');
});