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
                console.log('Incorrect username');
                return done(null, false, {msg: 'Incorrect username'});
            }
            User.findById(user._id, function (err, result) {
                if (err) {
                    console.log(err);
                } else {
                    if (result) {
                        console.log('In valid:------');
                        if (pass == result.pass) {
                            console.log('Pass correct, Welcome');
                            return done(null, user, {msg: 'Pass correct, Welcome'});
                        } else {
                            console.log('Incorrect password');
                            return done(null, false, {msg: 'Incorrect password'});
                        }
                    }
                    else {
                        console.log('User not found');
                        return done(null, false, {msg: 'User not found'});
                    }
                }
            });
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
