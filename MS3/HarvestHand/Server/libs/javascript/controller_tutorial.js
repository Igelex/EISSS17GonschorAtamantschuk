/**
 * Created by Pastuh on 02.05.2017.
 */
var Tutorial = require('../models_mongoose/tutorial'),
    controller_entry = require('./controller_entry');

//Tutorial wird angelegt in DB
module.exports.addTutorial = function (tutorial, entry_id) {
    var newTutorial = new Tutorial(tutorial);/*({
        air_temp: {
            status: newTutorial.air_temp.status,
            deviation: newTutorial.air_temp.deviation,
            norm: newTutorial.air_temp.norm
        },
        air_moisture: {
            status: newTutorial.air_moisture.status,
            deviation: newTutorial.air_moisture.deviation,
            norm: newTutorial.air_moisture.norm
        },
        /!*Wasserbedarf l/m^2/Woche*!/
        soil_moisture: {
            status: newTutorial.soil_moisture.status,
            deviation: newTutorial.soil_moisture.deviation,
            water_requirements: newTutorial.soil_moisture.water_requirements,
            norm: newTutorial.soil_moisture.water_requirements
        },
        soil: {
            status: newTutorial.soil.status,
            norm: newTutorial.soil.norm
        },
        soil_temp: {
            status: newTutorial.soil_temp.status,
            deviation: newTutorial.soil_temp.deviation,
            norm: newTutorial.soil_temp.norm
        },
        ph_value: {
            status: newTutorial.ph_value.status,
            deviation: newTutorial.ph_value.deviation,
            norm: newTutorial.ph_value.norm
        },
        height_meter: {
            status: newTutorial.height_meter.status,
            deviation: newTutorial.height_meter.deviation,
            norm: newTutorial.height_meter.norm
        },
        mature_after_month: newTutorial.mature_after_month
    });*/
    newTutorial.save(function (err, result) {
        if (err) {
            console.log(err);
        } else {
            console.log('Tutorial was saved ' + result);
            //direkt wird die @tutorial_id im Eintrag aktualisiert
            controller_entry.updateEntryTutorialId(entry_id, result._id);
        }
    });
};

//Zum Eintrag Passendes Tutorial wird gefunden
module.exports.getTutorialById = function (req, res) {
    Tutorial.findById(req.params.id, function (err, result) {
        if (err) {
            res.status(500).type('text').write("DB error: " + err);
        } else {
            if (result) {
                console.log("GetTutorial bi ID: " + result);
                res.status(200).type('application/json').send(result);
            }
            else {
                res.status(204).type('text').send('No Tutorial found');
            }
        }
    });
};

module.exports.deleteTutorial = function (id) {
    Tutorial.findByIdAndRemove(id, function (err, result) {
        console.info(result);
        if (err) {
            res.status(500).type('text').write("DB error: " + err);
        } else {
            if (result) {
                res.status(200).send('Tutorial with id: ' + result._id + ' succesfully deleted');
            } else {
                res.status(204 ).send('Tutorial with id: ' + id + ' not found');
            }
        }

    });
};

//debugging
module.exports.getAllTutorials = function (req, res) {
    Tutorial.find().exec(function (err, result) {
        if (err) {
            console.log(err);
            res.status(500).type('text').send('DB error :' + err);
        } else {
            if (Object.keys(result).length > 0) {
                res.status(200).type('application/json').send(result);
            }
            else {
                res.status(200).type('text').send('No Tutorials found');
            }
        }
    })
};