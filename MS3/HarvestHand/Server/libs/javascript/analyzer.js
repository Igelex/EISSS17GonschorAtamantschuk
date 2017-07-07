/**
 * Created by Pastuh on 12.05.2017.
 */
var controller_tutorial = require('./controller_tutorial'),
    http = require('http'),
    weather = require('./weather'),
    Norm = require('../models_mongoose/norms'),
    entry,
    currentNorm,
    NORM = 0, //flag, wenn daten von der Norm nicht abweichen
    LESS = 1, //flag, wenn daten kleiner als die Norm
    GREATER = 2; //flag, wenn daten größer als die Norm

var newTutorial = {
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
    /*Standardwerte(Norms) bestimmter PFlanze werden aus der Datenbank geholt*/
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
                    /**
                     * Als nächstes wird der Niderschlag für eine Woche requested in dem Modul 'weather'
                     * @param entry.location.countryISOCode - DE, SN...
                     * @param entry.location.city - die Stadt
                     */
                    weather.getPrecipitationForWeek(entry.location.countryISOCode, entry.location.city);
                }
                else {
                    console.error("Cant save Tutorila, NORM is: " + currentNorm);
                }
            }

        });
};


/**
 * Vergleicht Daten des Eitrags mit den dazugehörigen Normen, für jeden wert wird flag gesetzt und die Abweichnung in
 * prozent berechnet, die Funktion wird im Modul 'weather' aufgerufen, nachdem die Wetterdaten abgefragt wurden
 * @param weekPrecipitation - Wochenniederschlag
 */
module.exports.analyseValues = function (weekPrecipitation) {

    /*aktueller Wert jeder Eigenschaft wird mit dem Norm-Wert abgeglichen, um Abweichungen zu ermitteln*/

    /*Air temperature Analyse*/
    //Überprüfen ob der Wert kleiner ist als die Norm
    if (entry.air_temp < currentNorm.air_temp.min) {
        //Prozentuelle abweichung wird Berechnet
        newTutorial.air_temp.deviation = calculateDeviation(currentNorm.air_temp.min, entry.air_temp);
        newTutorial.air_temp.status = LESS;
        //Überprüfen ob der Wert größer ist als die Norm
    } else if (entry.air_temp > currentNorm.air_temp.max) {
        //Prozentuelle abweichung wird Berechnet
        newTutorial.air_temp.deviation = calculateDeviation(entry.air_temp, currentNorm.air_temp.max);
        newTutorial.air_temp.status = GREATER;
        //Anderfalls ist der Wert in Ordnung
    } else {
        newTutorial.air_temp.deviation = 0;
        newTutorial.air_temp.status = NORM
    }
    //aktueller und gewünschter Werte werden gespeichert
    newTutorial.air_temp.currentValue = entry.air_temp;
    newTutorial.air_temp.norm = currentNorm.air_temp.min + "-" + currentNorm.air_temp.max;

    /*Air humidity Analyse*/
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

    //Wird ermittelt, ob der Bodentype passend ist
    /*Bodentyp Analyse*/
    if (entry.soil_id != currentNorm.soil.id) {
        newTutorial.soil.status = LESS;
    } else {
        newTutorial.soil.status = NORM;
    }
    newTutorial.soil.currentValue = entry.soil_id;
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
    /*Zu Bodenfeuchtigkeit wird dazu die empfohlen wöchentliche Wassermenge zum Gießen berechnet */
    //Wasserverbrauch der Pflanze pro m^2 * Fläche - Niederschlag = wöchentliche Wassermenge
    newTutorial.soil_moisture.water_requirements = currentNorm.water_requirements * entry.area - weekPrecipitation;
    newTutorial.mature_after_month = currentNorm.mature_after_month;
    newTutorial.crop_name = currentNorm.crop_name;

    console.log("New Tutorial erstellt... ");
    controller_tutorial.addTutorial(newTutorial, entry._id);
};

/**
 * Berechnet um wieviel Prozent @b ist kleiner als @a. @a ist 100%.(Beispiel: @a = 50 =100%, @b ist 30, also 60% von @a,
 * heisst die @b ist um 100-60=40 kleiner als @a, demensprechend ist die Abweichung 40%)
 * @param a - größere Zahl, Abweichnug von dieser Zahl wird berechnet
 * @param b - kleinere Zahl
 * @returns {int} - abgerundete Abweichungszahl(z.B. 40)
 */
function calculateDeviation(a, b) {
    return ((a - b) / a * 100).toFixed(0);

}