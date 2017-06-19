/**
 * Created by Pastuh on 02.05.2017.
 */
var mongoose = require('mongoose');

//Users collection
var userSchema = mongoose.Schema({
    name: {type: String, required: false},
    email: {type: String, unique: true},
    pass: {type: String, required: false},
    user_type: {type: Number, required: true}, /*TYPE_PROFI = 0, TYPE_USER = 1*/
    phone_number: {type: Number, unique: true}
});

module.exports = mongoose.model('user', userSchema);