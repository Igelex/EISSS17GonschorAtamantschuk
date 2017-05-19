/**
 * Created by Pastuh on 18.05.2017.
 */
var passport = require('passport'),
    express = require('express'),
    LocalStrategy = require('passport-local').Strategy,
    User = require('../models_mongoose/user');

passport.use(new LocalStrategy({
        usernameField: 'email',
        passwordField: 'pass'
    },
    function (email, pass, done) {
        console.log('Email: ' + email);
        User.findOne({'email': email}, function (err, user) {
            if (err) {
                return done(err);
            }
            if (!user) {
                console.log('In !user: ' + user);
                return done(null, false, {message: 'Incorrect username.'});
            }
            User.findById(user._id, function (err, result) {
                if (err) {
                    console.log(err);
                } else {
                    if (result) {
                        console.log('In valid: ' + result.pass);
                        if (pass == result.pass) {
                            return done(null, user);
                        } else {
                            return done(null, false, {message: 'Incorrect password.'});
                        }
                    }
                    else {
                        return done(null, false, {message: 'User not found'});
                    }
                }
            });
        });
    }
));

passport.serializeUser(function (user, done) {
    console.log(user.id);
    done(null, user.id);
});

passport.deserializeUser(function (id, done) {
    User.findById(id, function (err, user) {
        console.log(user);
        done(err, user);
    });
});
