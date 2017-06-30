/**
 * Created by Pastuh on 12.05.2017.
 */
var controller_tutorial = require('./controller_tutorial'),
    http = require('http'),
    weather = require('./weather'),
    Norm = require('../models_mongoose/norms'),
    weekPrecipitation = 0,
    entry,
    currentNorm
    NORM = 0, //flag, wenn daten von der Norm nicht abweichen
    LESS = 1, //flag, wenn daten kleiner als die Norm
    GREATER = 2; //flag, wenn daten größer als die Norm

var newTutorial = {
    air_temp: {
        status: Number,
        deviation: Number,
        currentValue: Number,
        norm: String
    },
    air_humidity: {
        status: Number,
        deviation: Number,
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
};

//Analysiert Daten eines Eintrags, wird beim erstellen des Eintrags aufgerufen
/*Standardwerte werden aus der Datenbank geholt*/
module.exports.analyseData = function (currentEntry) {
    /*Standardwerte werden aus der Datenbank geholt*/
    Norm.findOne({
            crop_id: currentEntry.crop_id
        },
        function (err, norm) {
            if (err) {
                console.error("GetNorm DB Error:" + err);
            } else {
                if (norm) {
                    currentNorm = norm;
                    entry = currentEntry;
                    fetchWeatherData(entry.location.countryISOCode, entry.location.city);
                }
                else {
                    console.error("Cant save Tutorila, NORM is: " + currentNorm);
                }
            }

        });
};

function fetchWeatherData(countryISOCode, city) {
    weather.getPrecipitationForWeek(countryISOCode, city);
}

/*Vergleicht Daten des Eitrags mit den dazugehörigen Normen, für jeden wert wird flag gesetzt und die Abweichnung in
 prozent berechnet*/
module.exports.analyseValues = function (weekPrecipitation) {

    console.log('Percip in AnalyseValues: ' + weekPrecipitation);
    /*Air temperature Analyse*/
    if (entry.air_temp < currentNorm.air_temp.min) {
        newTutorial.air_temp.deviation = calculateDeviation(currentNorm.air_temp.min, entry.air_temp);
        newTutorial.air_temp.status = LESS;
    } else if (entry.air_temp > currentNorm.air_temp.max) {
        newTutorial.air_temp.deviation = calculateDeviation(entry.air_temp, currentNorm.air_temp.max);
        newTutorial.air_temp.status = GREATER;
    } else {
        newTutorial.air_temp.deviation = 0;
        newTutorial.air_temp.status = NORM
    }
    newTutorial.air_temp.currentValue = entry.air_temp;
    newTutorial.air_temp.norm = currentNorm.air_temp.min + "-" + currentNorm.air_temp.max;

    /*Air moisture Analyse*/
    if (entry.air_humidity < currentNorm.air_humidity.min) {
        newTutorial.air_humidity.deviation = calculateDeviation(currentNorm.air_humidity.min, entry.air_humidity);
        newTutorial.air_humidity.status = LESS;
    } else if (entry.air_humidity > currentNorm.air_humidity.max) {
        newTutorial.air_humidity.deviation = calculateDeviation(entry.air_humidity, currentNorm.air_humidity.max);
        newTutorial.air_humidity.status = GREATER;
    } else {
        newTutorial.air_humidity.deviation = 0;
        newTutorial.air_humidity.status = NORM
    }
    newTutorial.air_humidity.currentValue = entry.air_humidity;
    newTutorial.air_humidity.norm = currentNorm.air_humidity.min + "-" + currentNorm.air_humidity.max;

    /*Soil temperature Analyse*/
    if (entry.soil_temp < currentNorm.soil_temp.min) {
        newTutorial.soil_temp.deviation = calculateDeviation(currentNorm.soil_temp.min, entry.soil_temp);
        newTutorial.soil_temp.status = LESS;
    } else if (entry.soil_temp > currentNorm.soil_temp.max) {
        newTutorial.soil_temp.deviation = calculateDeviation(entry.soil_temp, currentNorm.soil_temp.max);
        newTutorial.soil_temp.status = GREATER;
    } else {
        newTutorial.soil_temp.deviation = 0;
        newTutorial.soil_temp.status = NORM;
    }
    newTutorial.soil_temp.currentValue = entry.soil_temp;
    newTutorial.soil_temp.norm = currentNorm.soil_temp.min + "-" + currentNorm.soil_temp.max;

    /*Height Analyse*/
    if (entry.height_meter < currentNorm.height_meter.min) {
        newTutorial.height_meter.deviation = calculateDeviation(currentNorm.height_meter.min, entry.height_meter);
        newTutorial.height_meter.status = LESS;
    } else if (entry.height_meter > currentNorm.height_meter.max) {
        newTutorial.height_meter.deviation = calculateDeviation(entry.height_meter, currentNorm.height_meter.max);
        newTutorial.height_meter.status = GREATER;
    } else {
        newTutorial.height_meter.deviation = 0;
        newTutorial.height_meter.status = NORM;
    }
    newTutorial.height_meter.currentValue = entry.height_meter;
    newTutorial.height_meter.norm = currentNorm.height_meter.min + "-" + currentNorm.height_meter.max;

    /*PH Analyse*/
    if (entry.ph_value < currentNorm.ph_value) {
        newTutorial.ph_value.deviation = calculateDeviation(currentNorm.ph_value, entry.ph_value);
        newTutorial.ph_value.status = LESS;
    } else if (entry.ph_value > currentNorm.ph_value) {
        newTutorial.ph_value.deviation = calculateDeviation(entry.ph_value, currentNorm.ph_value);
        newTutorial.ph_value.status = GREATER
    } else {
        newTutorial.ph_value.deviation = 0;
        newTutorial.ph_value.status = NORM;
    }
    newTutorial.ph_value.currentValue = entry.ph_value;
    newTutorial.ph_value.norm = currentNorm.ph_value;

    /*Bodentyp Anylyse*/
    if (entry.soil != currentNorm.soil.id) {
        newTutorial.soil.status = LESS;
    } else {
        newTutorial.soil.status = NORM;
    }
    newTutorial.soil.currentValue = entry.soil;
    newTutorial.soil.norm = currentNorm.soil.id;

    if (entry.soil_moisture < currentNorm.soil_moisture.min) {
        newTutorial.soil_moisture.deviation = calculateDeviation(currentNorm.soil_moisture.min, entry.soil_moisture);
        newTutorial.soil_moisture.status = LESS;
    } else if (entry.soil_moisture > currentNorm.soil_moisture.max) {
        newTutorial.soil_moisture.deviation = calculateDeviation(entry.soil_moisture, currentNorm.soil_moisture.max);
        newTutorial.soil_moisture.status = GREATER;
    } else {
        newTutorial.soil_moisture.deviation = 0;
        newTutorial.soil_moisture.status = NORM;
    }
    newTutorial.soil_moisture.currentValue = entry.soil_moisture;
    newTutorial.soil_moisture.norm = currentNorm.soil_moisture.min + "-" + currentNorm.soil_moisture.max;
    newTutorial.soil_moisture.water_requirements = currentNorm.water_requirements * entry.area - weekPrecipitation;
    newTutorial.mature_after_month = currentNorm.mature_after_month;

    console.log("New Tutorial erstellt: " + newTutorial.air_humidity.currentValue);
    controller_tutorial.addTutorial(newTutorial, entry._id);
}

function calculateDeviation(a, b) {
    return ((a - b) / a * 100).toFixed(0);

}