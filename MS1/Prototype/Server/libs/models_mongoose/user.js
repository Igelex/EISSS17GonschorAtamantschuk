/**
 * Created by Pastuh on 02.05.2017.
 */
var mongoose = require('mongoose');

//Users collection

var userSchema = mongoose.Schema({
    first_name: String,
    last_name: String,
    gender: String,
    email: {type: String, unique: true, required: true},
    pass: String,
    user_type: Number /*TYPE_PROFI = 1, TYPE_USER = 0*/
});

module.exports = mongoose.model('user', userSchema);