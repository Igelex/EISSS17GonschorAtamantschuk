/**
 * Created by Pastuh on 02.05.2017.
 */
var Entry = require('../models_mongoose/entry'),
    analyzer = require('./analyzer'),
    controller_tutorial = require('./controller_tutorial');

//Neuen Eintrag in der DB speichern, parralel werden die Daten ausgewertet
module.exports.addEntry = function (req, res) {
    var newEntry = new Entry(req.body)
    newEntry.save(function (err, result) {
        if (err) {
            console.log(err);
            res.status(500).type('text').send({msg: 'Entry not saved', res: false});
        } else {
            console.log('Entry saved: ' + result);
            res.status(200).type('text').send({msg: 'Entry saved', res: true});
            /*Wenn Entry gespeichert, anyalysiere Daten*/
            console.log('Starte Bodenanalyse...');
            analyzer.analyseData(result);
        }
    });
};

//Alle Entries anzeigen eisen Users anzeigen
module.exports.getEntries = function (req, res) {
    console.log("Get Entries owner_id: " + req.query.owner_id);
    console.log("Get Entries number: " + req.query.phone_number);
    Entry.find({
            $or: [{owner_id: req.query.owner_id}, {collaborators: req.query.phone_number}]
        },
        {
            "entry_name": true,
            "art_id": true, "area": true,
            "location": true,
            "crop_id": true
        }, function (err, result) {
            if (err) {
                console.log(err);
                res.status(500).type('text').send({msg: 'DB Error', res: false});
            } else {
                if (result) {
                    console.log("Found Entries: " + result);
                    res.status(200).type('application/json').send(result);
                }
                else {
                    res.status(204).type('application/json').send({msg: 'No Entries found', res: false});
                }
            }
    })
};

//Bestimmten Entry anzeigen
module.exports.getEntryById = function (req, res) {
    console.info(req.params.id);
    Entry.findById(req.params.id, function (err, result) {
        if (err) {
            res.status(500).type('text').send({msg: 'DB Error', res: false});
        } else {
            if (result) {
                res.status(200).type('application/json').send(result);
            }
            else {
                res.status(200).type('application/json').send({msg: 'No Entry found', res: false});
            }
        }
    });
};

//Daten aktualisieren
module.exports.updateEntry = function (req, res) {
    Entry.findByIdAndUpdate(req.params.id, req.body, function (err, result) {
        console.info(result);
        if (err) {
            res.status(500).type('text').send({msg: 'DB Error', res: false});
        } else {
            if (result) {
                res.status(200).type('application/json').send({
                    msg: 'Entry with id: ' + result._id + ' successfully updated',
                    res: true
                });
            } else {
                res.status(204).type('application/json').send({
                    msg: 'Entry with id: ' + req.params.id + ' not found',
                    res: false
                });
            }
        }

    });
};

//Wenn daten analysiert und Tutorial erstellt, wird die @tutorial_id im Entry aktualisiert
module.exports.updateEntryTutorialId = function (id, tutorial_id) {
    Entry.findByIdAndUpdate(id, {tutorial_id: tutorial_id}, function (err, result) {
        if (err) {
            console.error("DB error: " + err);
        } else {
            if (result) {
                console.log("Update Entry tutorial_id: " + result._id);
            } else {
                console.error('Entry tutorial_id not updated');
            }
        }

    });
};

module.exports.deleteEntry = function (req, res) {
    Entry.findByIdAndRemove(req.params.id, function (err, result) {
        console.info(result);
        if (err) {
            res.status(500).type('application/json').send({msg: "DB error: " + err, res: false});
        } else {
            if (result) {
                res.status(200).type('application/json').send({
                    msg: 'Entry with id: ' + result._id + ' successfully deleted',
                    res: true
                });
            } else {
                res.status(200).type('application/json').send({
                    msg: 'Entry with id: ' + req.params.id + ' not found',
                    res: false
                });
            }
        }

    });
};
