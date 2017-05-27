/**
 * Created by Pastuh on 02.05.2017.
 */
var mongoose = require('mongoose');

//Users collection

var userSchema = mongoose.Schema({
    name: {type: String, required: true},
    gender: String,
    email: {type: String, unique: true, required: true},
    pass: {type: String, required: true},
    user_type: {type: Number, required: true} /*TYPE_PROFI = 1, TYPE_USER = 0*/
});

module.exports = mongoose.model('user', userSchema);