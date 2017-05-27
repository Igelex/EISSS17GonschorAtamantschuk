/**
 * Created by Pastuh on 02.05.2017.
 */
var mongoose = require('mongoose');

//Eintrag collection, hier werden Daten des Ackers gespeichert

var entrySchema = mongoose.Schema({
    entry_name: {type: String, required: true},
    art_id: {type: Number, required: true},
    tutorial_id: String,
    owner_id: {type: String, required: true},
    collaborators:[String],
    ph_value: Number,
    water: Number,
    minerals: Number
});

module.exports = mongoose.model('entry', entrySchema);