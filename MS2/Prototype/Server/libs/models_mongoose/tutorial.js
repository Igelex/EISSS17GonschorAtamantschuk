/**
 * Created by Pastuh on 02.05.2017.
 */
var mongoose = require('mongoose');

//Tutorial collection zum speichern der Anleitungen

var tutorialSchema = mongoose.Schema({
    air_temp: {
        status: Number,
        deviation: Number,
        norm: Number
    },
    air_moisture: {
        status: Number,
        deviation: Number,
        norm: Number
    },
    /*Wasserbedarf l/m^2/Woche*/
    soil_moisture: {
        status: Number,
        deviation: Number,
        water_requirements: Number,
        norm: Number
    },
    soil: {
        status: Number,
        norm: Number
    },
    soil_temp: {
        status: Number,
        deviation: Number,
        norm: Number
    },
    ph_value: {
        status: Number,
        deviation: Number,
        norm: Number
    },
    height_meter: {
        status: Number,
        deviation: Number,
        norm: Number
    },
    mature_after_month: Number
    /*.
      .
      .
      .
    */
});

module.exports = mongoose.model('tutorial', tutorialSchema);