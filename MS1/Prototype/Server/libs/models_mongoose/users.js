/**
 * Created by Pastuh on 02.05.2017.
 */
var mongoose = require('mongoose');

var userSchema = mongoose.Schema({
    first_name: String,
    last_name: String
    /*gender: String,
    birth_year: String,
    email: String,
    pass: String,
    picture: Number,
    type: Number*/
});

module.exports = mongoose.model('users', userSchema);