/**
 * Created by Pastuh on 02.05.2017.
 */
var mongoose = require('mongoose');

//Eintrag collection, hier werden Daten des Ackers gespeichert

var entrySchema = mongoose.Schema({
    /*Ein beschreibender Name*/
    entry_name: {type: String, required: true},
    /*Art der Pflanze, mit id identifiziert*/
    crop_id: {type: Number, required: true},
    /*Verknüpfter Tutorial*/
    tutorial_id: String,
    /*ID des Erstellers*/
    owner_id: {type: String, required: true},
    /*Colaborators, die den Zugriff auf den Eintrag haben*/
    collaborators_id:Array,
    collaborators_number:Array,
    /*
    * Properties
    */
    location: {
        name: {type: String, required: true},
        countryISOCode: {type: String, required: true},
        city: {type: String, required: true}
    },
    /*Fläche in m2*/
    area: {type: Number, required: true},
    /*Art des Bodens*/
    soil_id: {type: Number, required: true},
    air_temp: {type: Number, required: true},
    soil_temp: {type: Number, required: true},
    soil_moisture: {type: Number, required: true},
    air_humidity: {type: Number, required: true},
    ph_value: {type: Number, required: true},
    /*Anbauhöhe über dem Meerspiegel*/
    height_meter: {type: Number, required: true}

});

module.exports = mongoose.model('entry', entrySchema);