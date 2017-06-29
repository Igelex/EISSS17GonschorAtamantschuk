/**
 * Created by Pastuh on 02.05.2017.
 */
var express = require('express'),
    app = express(),
    cookieParser = require('cookie-parser'),
    bodyParser = require('body-parser'),
    mongoose = require('mongoose'),
    session = require('express-session'),
    MongoStore = require('connect-mongo')(session),
    passport = require('passport'),
    flash = require('connect-flash'),
    controller_entry = require('./libs/javascript/controller_entry'),
    controller_norm = require('./libs/javascript/controller_norms'),
    controller_tutorial = require('./libs/javascript/controller_tutorial'),
    controller_user = require('./libs/javascript/controller_user'),
    routes = require('./libs/routes/index'),
    weather = require('./libs/javascript/weather'),
    port = 3001;

/*Ablauf: POST Entry --> analyzer --> GET Norms --> analyseValues --> save Tutorial
 *--> update @tutorial_id in Entry */

//Check DB Connection
mongoose.connect('mongodb://localhost/db');
var db = mongoose.connection;
db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function () {
    console.log('DB connected');
});

require('./libs/javascript/passport');

//all environments
app.set('port', process.env.PORT || port);
app.use(bodyParser.json());
app.use(cookieParser());
app.use(session({
    secret: 'my secret',
    resave: false,
    saveUninitialized: true,
    store: new MongoStore({mongooseConnection: db})
}));
app.use(flash());
app.use(passport.initialize());
app.use(passport.session());


app.use(function (req, res, next) {
    res.set('X-Powered-By', 'HarvestHand');
    next();
});

//Test
app.get('/', function (req, res) {
    res.send('Hallo, World!!!');
});

app.get("/weather", weather.getAirTemp);

//////////////////////////Users

app.post('/signup', controller_user.registerUser);
app.post('/signin', controller_user.logIn);
/*app.post('/signin',
    passport.authenticate('local'),
    function (req, res) {
        res.status(200).send({_id: req.user._id, user_type: req.user.user_type});
    });*/
app.put('/users/:id', controller_user.updateUser);
app.get('/users/:id', controller_user.getUserById);
app.get('/users', controller_user.getUsers);
app.delete('/users/:id', controller_user.deleteUser);
//////////////////////////Users

//////////////////////////Entries

app.put('/entries/:id' , controller_entry.updateEntry);
app.get('/entries', controller_entry.getEntries);
app.get('/entries/:id',  controller_entry.getEntryById);
app.post('/entries', controller_entry.addEntry);
app.delete('/entries/:id', controller_entry.deleteEntry);
//Hole Tutorial zum bestimmten Eintrag
app.get('/entries/:id/tutorial/:id', controller_tutorial.getTutorialById);

//////////////////////////Entries

app.post('/norms', function (req, res) {
    controller_norm.addNorm(req, res);
});

app.get('/norms/:id', function (req, res) {
    controller_norm.getNormById(req, res);
});
//Debugging!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

app.get('/norms', function (req, res) {
    controller_norm.getAllNorms(req, res);
});

app.get('/tutorials', function (req, res) {
    controller_tutorial.getAllTutorials(req, res);
});

//Debugging!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

app.listen(app.get('port'), function () {
    console.log('Server online');
});