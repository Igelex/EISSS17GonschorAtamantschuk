/**
 * Created by Pastuh on 12.05.2017.
 */
var controller_tutorial = require('./controller_tutorial'),
    http = require('http'),
    weather = require('./weather'),
    norms_controller = require('./controller_norms'),
    Norm = require('../models_mongoose/norms'),
    currentNorm,
    NORM = 0, //flag, wenn daten von der Norm nicht abweichen
    LESS = 1, //flag, wenn daten kleiner als die Norm
    GREATER = 2; //flag, wenn daten größer als die Norm

var newTutorial = {
    air_temp: {
        status: Number,
        deviation: Number,
        norm: String
    },
    air_moisture: {
        status: Number,
        deviation: Number,
        norm: String
    },
    /*Wasserbedarf l/m^2/Woche*/
    soil_moisture: {
        status: Number,
        deviation: Number,
        water_requirements: Number,
        norm: String
    },
    soil: {
        status: Number,
        norm: Number
    },
    soil_temp: {
        status: Number,
        deviation: Number,
        norm: String
    },
    ph_value: {
        status: Number,
        deviation: Number,
        norm: String
    },
    height_meter: {
        status: Number,
        deviation: Number,
        norm: String
    },
    mature_after_month: Number
};

//Analysiert Daten eines Eintrags, wird beim erstellen des Eintrags aufgerufen
module.exports.analyseData = function (entry) {


    Norm.findOne({
            crop_id: entry.crop_id
        },
        function (err, result) {
            if (err) {
                console.log("GetNorm DB Error:" + err);
            } else {
                if (result) {
                    currentNorm = result;
                    /*Air temperature Analyse*/
                    analyseValues(newTutorial.air_temp.deviation, newTutorial.air_temp.status, newTutorial.air_temp.norm,
                        entry.air_temp, currentNorm.air_temp.min, currentNorm.air_temp.max);
                    /*Air moisture Analyse*/
                    analyseValues(newTutorial.air_moisture.deviation, newTutorial.air_moisture.status,
                        entry.air_moisture, currentNorm.air_moisture.min, currentNorm.air_moisture.max);
                    /*Soil temperature Analyse*/
                    analyseValues(newTutorial.soil_temp.deviation, newTutorial.soil_temp.status,
                        entry.soil_temp, currentNorm.soil_temp.min, currentNorm.soil_temp.max);
                    /*Height Analyse*/
                    analyseValues(newTutorial.height_meter.deviation, newTutorial.height_meter.status,
                        entry.height_meter, currentNorm.height_meter.min, currentNorm.height_meter.max);

                    /*PH Analyse*/
                    if (entry.ph_value <= currentNorm.ph_value) {
                        newTutorial.ph_value.deviation = (entry.ph_value * 100) / currentNorm.ph_value;
                        newTutorial.ph_value.status = LESS;
                    } else if (entry.ph_value >= currentNorm.ph_value) {
                        newTutorial.ph_value.deviation = (currentNorm.ph_value * 100) / entry.ph_value;
                        newTutorial.ph_value.status = GREATER
                    } else {
                        newTutorial.ph_value.deviation = 100;
                        newTutorial.ph_value.status = NORM;
                    }
                    newTutorial.ph_value.norm = currentNorm.ph_value;

                    /*Bodentyp Anylyse*/
                    if (entry.soil != currentNorm.soil.id) {
                        newTutorial.soil.status = LESS;
                    } else {
                        newTutorial.soil.status = NORM;
                    }
                    newTutorial.soil.norm = currentNorm.soil.id;

                    var weekPrecipitation = 80; //weather.getPrecipitationForWeek(entry.location);

                    if (entry.soil_moisture <= currentNorm.soil_moisture.min) {
                        newTutorial.soil_moisture.deviation = (entry.soil_moisture * 100) / currentNorm.soil_moisture.min;

                        newTutorial.soil_moisture.status = LESS;
                    } else if (entry.soil_moisture <= currentNorm.soil_moisture.max) {
                        newTutorial.soil_moisture.deviation = (currentNorm.soil_moisture.max * 100) / entry.soil_moisture;
                        newTutorial.soil_moisture.status = GREATER;
                    } else {
                        newTutorial.soil_moisture.deviation = 100;
                        newTutorial.soil_moisture.status = NORM;
                    }
                    newTutorial.soil_moisture.norm = currentNorm.soil_moisture.min + "-" + currentNorm.soil_moisture.max;
                    newTutorial.water_requirements = currentNorm.water_requirements * entry.area - weekPrecipitation;
                    newTutorial.mature_after_month = currentNorm.mature_after_month;

                    console.log("New Tutorial: " + newTutorial.air_temp.status);
                    controller_tutorial.addTutorial(newTutorial);
                }
                else {
                    console.log("Cant save Tutorila, NORM is: " + currentNorm);
                }
            }

        });
}

/*Vergleicht Daten des Eitrags mit den dazugehörigen Normen, für jeden wert wird flag gesetzt und die Abweichnung in
 prozent berechnet*/
function analyseValues(newTutorialDeviation, newTutorialStatus, newTutorialNorm, entryValue, normValueMin, normValueMax) {

    if (entryValue <= normValueMin) {
        newTutorialDeviation = (entryValue * 100) / normValueMin;
        newTutorialStatus = LESS;
    } else if (entryValue >= normValueMax) {
        newTutorialDeviation = (normValueMax * 100) / entryValue;
        newTutorialStatus = GREATER
    } else {
        newTutorialDeviation = 100;
        newTutorialStatus = GREATER
    }
    newTutorialNorm = normValueMin + "-" + normValueMin;
}

/*var options = {
 host: 'localhost',
 port: '3000',
 path: '/norms/' + entry.art_id,
 method: 'GET'
 };

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
 };*/