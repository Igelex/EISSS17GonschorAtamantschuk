/**
 * Created by Pastuh on 12.05.2017.
 */
var controller_tutorial = require('./controller_tutorial'),
    http = require('http'),
    weather = require('./weather')
    norms_controller = require('./controller_norms')
NORM = 0, //flag, wenn daten von der Norm nicht abweichen
    LESS = 1, //flag, wenn daten kleiner als die Norm
    GREATER = 2; //flag, wenn daten größer als die Norm

var newTutorial = {
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
};

//Analysiert Daten eines Eintrags, wird beim erstellen des Eintrags aufgerufen
module.exports.analyseData = function (entry) {

    var currentNorm = norms_controller.getNorm(entry.art_id);

    if (currentNorm == null) {
        /*Air temperature Analyse*/
        analyseValues(newTutorial.air_temp.deviation, newTutorial.air_temp.status,
            entry.air_temp, norm.air_temp.min, norm.air_temp.max);
        /*Air moisture Analyse*/
        analyseValues(newTutorial.air_moisture.deviation, newTutorial.air_moisture.status,
            entry.air_moisture, norm.air_moisture.min, norm.air_moisture.max);
        /*Soil temperature Analyse*/
        analyseValues(newTutorial.soil_temp.deviation, newTutorial.soil_temp.status,
            entry.soil_temp, norm.soil_temp.min, norm.soil_temp.max);
        /*Height Analyse*/
        analyseValues(newTutorial.height_meter.deviation, newTutorial.height_meter.status,
            entry.height_meter, norm.height_meter.min, norm.height_meter.max);

        /*PH Analyse*/
        if (entry.ph_value <= norm.ph_value) {
            newTutorial.ph_value.deviation = (entry.ph_value * 100) / norm.ph_value;
            newTutorial.ph_value.status = LESS;
        } else if (entryValue >= normValue) {
            newTutorial.ph_value.deviation = (norm.ph_value * 100) / entry.ph_value;
            newTutorial.ph_value.status = GREATER
        } else {
            newTutorial.ph_value.deviation = 100;
            newTutorial.ph_value.status = NORM;
        }

        var weekPrecipitation = weather.getPrecipitationForWeek(entry.location);

        if (entry.soil_moisture <= norm.soil_moisture.min) {
            newTutorial.soil_moisture.deviation = (entry.soil_moisture * 100) / norm.soil_moisture.min;

            newTutorial.soil_moisture.status = LESS;
        } else if (entry.soil_moisture <= norm.soil_moisture.max) {
            newTutorial.soil_moisture.deviation = (norm.soil_moisture.max * 100) / entry.soil_moisture;
            newTutorial.soil_moisture.status = GREATER
        } else {
            newTutorial.soil_moisture.deviation = 100;
            newTutorial.soil_moisture.status = NORM;
        }
        newTutorial.water_requirements = norm.water_requirements * entry.area - weekPrecipitation;
        newTutorial.mature_after_month = norm.mature_after_month;
        console.log("New Tutorial: " + newTutorial);
        controller_tutorial.addTutorial(newTutorial);
    }


};


/*Vergleicht Daten des Eitrags mit den dazugehörigen Normen, für jeden wert wird flag gesetzt und die Abweichnung in
prozent berechnet*/
    function analyseValues(newTutorialDeviation, newTutorialStatus, entryValue, normValueMin, normValueMax) {

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