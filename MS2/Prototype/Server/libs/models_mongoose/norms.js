/**
 * Created by Pastuh on 02.05.2017.
 */
var mongoose = require('mongoose');

//Norms collection zum speichern der Standartwerten der Bodendaten abhängig von dem,
//was eingepflanzt wird

var normsSchema = mongoose.Schema({
    crop_id: Number,
    name: [String],
    air_temp: {
        max: Number,
        min:Number,

    },
    soil_temp: {
        max: Number,
        min:Number,
    },
    soil_moisture: Number,
    ph_value: Number
    /*.
      .
      .
      .
    */
});

module.exports = mongoose.model('norms', normsSchema);