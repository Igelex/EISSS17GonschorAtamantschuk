/**
 * Created by Pastuh on 02.05.2017.
 */
var mongoose = require('mongoose');

//Users collection
var userSchema = mongoose.Schema({
    user_type: {type: Number, required: true}, /*TYPE_PROFI = 0, TYPE_USER = 1*/
    phone_number: {type: String, required: true, unique: true}
});

module.exports = mongoose.model('user', userSchema);