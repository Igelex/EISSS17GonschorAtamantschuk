/**
 * Created by Pastuh on 02.05.2017.
 */
var mongoose = require('mongoose');

var tutorialSchema = mongoose.Schema({
    entry_id: String,
    entry_name: String,
    ph: Number,
    water: Number,
    minerals: Number
    /*.
      .
      .
      .
    */
});

module.exports = mongoose.model('tutorial', tutorialSchema);