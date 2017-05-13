/**
 * Created by Pastuh on 02.05.2017.
 */
var mongoose = require('mongoose');

var normsSchema = mongoose.Schema({
    norm_name: String,
    art_id: Number,
    ph_norm: Number,
    water_norm: Number,
    minerals_norm: Number
    /*.
      .
      .
      .
    */
});

module.exports = mongoose.model('norms', normsSchema);