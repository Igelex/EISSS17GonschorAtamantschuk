/**
 * Created by Pastuh on 02.05.2017.
 */
var express = require('express'),
    app = express(),
    bodyParser = require('body-parser'),
    mongoose = require('mongoose'),
    controller_users = require('./libs/javascript/controller_users'),
    port = 3000;

app.use(bodyParser.json());

//Check DB Connection
mongoose.connect('mongodb://localhost/db');
var db = mongoose.connection;
db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function() {
    console.log('DB connected');
});

app.get('/', function (req, res) {
    res.send('Hallo, suchki!!!');
});

app.post('/users', function (req, res) {
    controller_users.addUser(req, res);
});

app.listen(port, function () {
    console.log('Server online');
});