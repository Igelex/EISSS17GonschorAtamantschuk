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
    port = 3000;

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
app. get('/', function (req, res) {
    res.send('Hallo, World!!!');
});

//////////////////////////Users

//app.use('/users/signinerror', routes);

app.get('/users/signinerror', function(req, res){
    var errmsg = req.flash('error');
    res.status(200).type('application/json').send({error_msg: errmsg });
});

app.post('/signup', controller_user.registerUser);

app.post('/users/signin',
    passport.authenticate('local',{
        failureRedirect: '/users/signinerror',
        failureFlash: true
    }),
    function (req, res) {
        //console.log('User_id: ' + req.session.lastNumber);
        res.status(200).send(req.user._id);
    });

app.get('/users/:id', controller_user.getUserById);
//////////////////////////Users


//////////////////////////Entries

app.get('/entries', controller_entry.getAllEntries);

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

//Hole Tutorial zum bestimmten Eintrag
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

app.listen(app.get('port'), function () {
    console.log('Server online');
});