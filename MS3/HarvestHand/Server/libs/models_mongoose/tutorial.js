/**
 * Created by Pastuh on 02.05.2017.
 */
var mongoose = require('mongoose');

/*Tutorial collection zum speichern der Anleitungen, zur jeder eingenschaft werden der Status(GREATER,LESS,NORM),
prozentuelle Abweichung von der Norm, aktueller Wert und der gewünschte Wert(@norm), und optionale Werte gespeichert*/

var tutorialSchema = mongoose.Schema({
    crop_name: String,
    air_temp: {
        status: Number,
        deviation: Number,
        currentValue: Number,
        norm: String
    },
    air_humidity: {
        status: Number,
        deviation: Number,
        currentValue: Number,
        norm: String
    },
    /*Wasserbedarf l/m^2/Woche*/
    soil_moisture: {
        status: Number,
        deviation: Number,
        currentValue: Number,
        water_requirements: Number,
        norm: String
    },
    soil: {
        status: Number,
        currentValue: Number,
        norm: Number
    },
    soil_temp: {
        status: Number,
        deviation: Number,
        currentValue: Number,
        norm: String
    },
    ph_value: {
        status: Number,
        deviation: Number,
        currentValue: Number,
        norm: String
    },
    height_meter: {
        status: Number,
        deviation: Number,
        currentValue: Number,
        norm: String
    },
    mature_after_month: Number
    /*.
      .
      .
      .
    */
});

module.exports = mongoose.model('tutorial', tutorialSchema);