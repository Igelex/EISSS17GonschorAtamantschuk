/**
 * Created by Pastuh on 02.05.2017.
 */
var mongoose = require('mongoose');

//Eintrag collection, hier werden Daten des Ackers gespeichert

var entrySchema = mongoose.Schema({
    /*Ein beschreibender Name*/
    entry_name: {type: String, required: true},
    /*Art der Pflanze, durch id identifiziert*/
    art_id: {type: Number, required: true},
    /*Verknüpfter Tutorial*/
    tutorial_id: String,
    /*ID des Erstellers*/
    owner_id: {type: String, required: true},
    /*Colaborators, die den Zugriff auf den Eintrag haben*/
    collaborators:[String],
    /*
    * Properties
    */
    surface: Number,
    ph_value: Number,
    water: Number,
    minerals: Number
});

module.exports = mongoose.model('entry', entrySchema);