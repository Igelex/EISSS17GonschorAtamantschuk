/**
 * Created by Pastuh on 02.05.2017.
 */
var Tutorial = require('../models_mongoose/tutorial'),
    controller_entry = require('./controller_entry');

//Tutorial wird angelegt in DB
module.exports.addTutorial = function (tutorial, entry_id) {
    var newTutorial = new Tutorial(tutorial);
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

//Zum Eintrag passendes Tutorial wird gesucht
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
                res.status(202).type('text').send({});
            }
        }
    });
};
/**
 * Tutorial löschen
 * @param id - @tutorial_id des Entrys, welchem Tutorial zugehört
 */
module.exports.deleteTutorial = function (id) {
    console.log("DELETE Tutorial ID: " + id);
    Tutorial.findByIdAndRemove(id, function (err, result) {
        if (err) {
            console.log("DELETE: Tutorial: " + err);
        } else {
            if (result) {
                console.log("DELETE: Tutorial deleted with ID: " + result._id);
            } else {
                console.log("DELETE: Tutorial not found with ID: " + id);
            }
        }

    });
};

//For debugging
module.exports.getAllTutorials = function (req, res) {
    Tutorial.find().exec(function (err, result) {
        if (err) {
            console.log(err);
            res.status(500).type('text').send('DB error :' + err);
        } else {
            if (result) {
                res.status(200).type('application/json').send(result);
            }
            else {
                res.status(200).type('text').send('No Tutorials found');
            }
        }
    })
};