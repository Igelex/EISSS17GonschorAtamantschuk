/**
 * Created by Pastuh on 02.05.2017.
 */
var mongoose = require('mongoose');

var entrySchema = mongoose.Schema({
    entry_name: String,
    art_id: Number,
    ph_value: Number
    /*gender: String,
    birth_year: String,
    email: String,
    pass: String,
    picture: Number,
    type: Number*/
});

module.exports = mongoose.model('users', entrySchema);