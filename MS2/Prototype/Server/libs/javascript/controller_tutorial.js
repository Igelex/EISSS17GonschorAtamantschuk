/**
 * Created by Pastuh on 02.05.2017.
 */
var Tutorial = require('../models_mongoose/tutorial'),
    controller_entry = require('./controller_entry');

//Tutorial wird angelegt in DB
module.exports.addTutorial = function (entry_id, entry_name, ph, water, minerals) {
    var newTutorial = new Tutorial({
        entry_id: entry_id,
        entry_name: entry_name,
        ph: ph,
        water: water,
        minerals: minerals
    });
    newTutorial.save(function (err, result) {
        if (err) {
            console.log(err);
        } else {
            console.log('Tutorial saved ' + result);
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
            if (result != null) {
                res.status(200).type('application/json').send(result);
            }
            else {
                res.status(200).type('text').send('No Tutorial found');
            }
        }
    });
};

module.exports.deleteTutorial = function (req, res) {
    Tutorial.findByIdAndRemove(req.params.id, function (err, result) {
        console.info(result);
        if (err) {
            res.status(500).type('text').write("DB error: " + err);
        } else {
            if (result != null) {
                res.status(200).type('text').send('Tutorial with id: ' + result._id + ' succesfully deleted');
            } else {
                res.status(200).type('text').send('Tutorial with id: ' + req.params.id + ' not found');
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