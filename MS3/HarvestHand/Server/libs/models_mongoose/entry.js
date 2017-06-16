/**
 * Created by Pastuh on 02.05.2017.
 */
var mongoose = require('mongoose');

//Eintrag collection, hier werden Daten des Ackers gespeichert

var entrySchema = mongoose.Schema({
    /*Ein beschreibender Name*/
    entry_name: {type: String, required: true},
    /*Art der Pflanze, mit id identifiziert*/
    art_id: {type: Number, required: true},
    /*Verknüpfter Tutorial*/
    tutorial_id: String,
    /*ID des Erstellers*/
    owner_id: {type: String, required: true},
    /*Colaborators, die den Zugriff auf den Eintrag haben*/
    collaborators:{type:Array, default:[]},

    /*
    * Properties
    */

    location: String,
    /*Fläche in ha*/
    area: Number,
    /*Art des Bodens*/
    soil: Number,
    air_temp: Number,
    soil_temp: Number,
    soil_moisture: Number,
    air_moisture: Number,
    ph_value: Number,
    /*Anbauhöhe über dem Meerspiegel*/
    height_meter: Number

});

module.exports = mongoose.model('entry', entrySchema);