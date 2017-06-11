/**
 * Created by Pastuh on 02.05.2017.
 */
var mongoose = require('mongoose');

//Norms collection zum speichern der Standartwerten der Bodendaten abhängig von dem,
//was eingepflanzt wird

var normsSchema = mongoose.Schema({
    crop_id: Number,
    name: [String],
    family: String,
    air_temp: {
        max: Number,
        min: Number
    },
    air_moisture: {
        max: Number,
        min: Number
    },
    /*Wasserbedarf l/m^2/Woche*/
    water_requirements: Number,
    fertilizer: {
        name: String,
        id: Number
    },
    soil: {
        name: String,
        id: Number
    },
    soil_temp: {
        max: Number,
        min:Number
    },
    soil_moisture: {
        max: Number,
        min: Number
    },
    ph_value: Number,
    height_meter: {
        max: Number,
        min: Number
    },
    mature_after_month: Number
    /*.
      .
      .
      .
    */
});

module.exports = mongoose.model('norms', normsSchema);