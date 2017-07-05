/* WIRD NICHT BENUTZT
var passport = require('passport'),
    express = require('express'),
    LocalStrategy = require('passport-local').Strategy,
    User = require('../models_mongoose/user');

passport.use(new LocalStrategy({
        usernameField: 'phone_number',
        passwordField: null
    },
    function (phone_number, done) {
        User.findOne({'phone_number': phone_number}, function (err, user) {
            if (err) {
                return done(err);
            }
            if (user) {
                console.log('Welcome');
                return done(null, user, {msg: 'Welcome'});
            } else {
                console.log('Incorrect NUmber');
                return done(null, false, {msg: 'Incorrect NUmber'});
            }
        });
    }
));

passport.serializeUser(function (user, done) {
    done(null, user.id);
});

passport.deserializeUser(function (id, done) {
    User.findById(id, function (err, user) {
        done(err, user);
    });
});
*/
