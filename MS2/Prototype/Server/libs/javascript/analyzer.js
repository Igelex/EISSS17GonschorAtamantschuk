/**
 * Created by Pastuh on 12.05.2017.
 */
var controller_tutorial = require('./controller_tutorial'),
    http  = require('http'),
    NORM = 0, //flag, wenn daten von der Norm nicht abweichen
    LESS = 1, //flag, wenn daten kleiner als die Norm
    GREATER = 2; //flag, wenn daten größer als die Norm

var ph,
    water,
    minerals;

//Analysiert Daten eines Eintrags, wird beim erstellen des Eintrags aufgerufen
module.exports.analyseData = function (entry) {
    var options = {
        host: 'localhost',
        port: '3000',
        path: '/norms/' + entry.art_id,
        method: 'GET'
    }

    //Request um die Norm-Datei zu holen,
    var externalRequest = http.request(options, function (externalResponse) {
        externalResponse.on('data', function (data) {
            //Helpermethode
            analyseValues(entry, JSON.parse(data));
            //schließlich wird Tutorial erstellt
            controller_tutorial.addTutorial(entry._id, entry.entry_name, ph, water, minerals);
        });
    });
    externalRequest.end();
    };

//Vergleicht Daten des Eitrags mit den dazugehörigen Normen, für jeden wert wird flag gesetzt
//Stark vereinfacht, alle Daten nur beispielhaft!!!
function analyseValues(entry, norm) {

    if ((entry.ph_value - norm.ph_norm) > 2) {
        ph = GREATER;
    } else if ((Math.abs(entry.ph_value - norm.ph_norm)) < -2) {
        ph = LESS;
    } else {
        ph = NORM;
    }
    if ((entry.water - norm.water_norm) >= 15) {
        water = GREATER;
    } else if ((entry.water - norm.water_norm) <= -15) {
        water = LESS;
    } else {
        water = NORM;
    }

    if ((entry.minerals - norm.minerals_norm) >= 10) {
        minerals = GREATER;
    } else if ((entry.minerals - norm.minerals_norm) <= -10) {
        minerals = LESS;
    } else {
        minerals = NORM;
    }
}