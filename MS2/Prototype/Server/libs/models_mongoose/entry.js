/**
 * Created by Pastuh on 02.05.2017.
 */
var mongoose = require('mongoose');

//Eintrag collection, hier werden Daten des Ackers gespeichert

var entrySchema = mongoose.Schema({
    entry_name: String,
    art_id: Number,
    tutorial_id: String,
    collaborators:[{collab_id: String}],
    ph_value: Number,
    water: Number,
    minerals: Number
    /*gender: String,
    birth_year: String,
    email: String,
    pass: String,
    picture: Number,
    type: Number*/
});

module.exports = mongoose.model('entry', entrySchema);