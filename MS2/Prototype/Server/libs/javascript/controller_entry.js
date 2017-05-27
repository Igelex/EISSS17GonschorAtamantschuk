/**
 * Created by Pastuh on 02.05.2017.
 */
var Entry = require('../models_mongoose/entry'),
    analyzer = require('./analyzer'),
    controller_tutorial = require('./controller_tutorial');

//Neuen Eintrag in der DB speichern, parralel werden die Daten ausgewertet
module.exports.addEntry = function (req, res) {
    var newEntry = new Entry({
        entry_name: req.body.entry_name,
        art_id: req.body.art_id,
        tutorial_id: req.body.tutorial_id,
        owner_id: req.body.owner_id,
        collaborators: req.body.collaborators,
        ph_value: req.body.ph_value,
        water: req.body.water,
        minerals: req.body.minerals
    });
    newEntry.save(function (err, result) {
        if (err) {
            console.log(err);
            res.status(500).type('text').send({msg: 'Entry not saved', res: false});
        } else {
            console.log('Entry saved');
            res.status(200).type('text').send({msg: 'Entry saved', res: true});
            //Wenn Entry gespeichert, anyalysiere Daten
            analyzer.analyseData(result._id);
        }
    });
};

//Alle Entries anzeigen
module.exports.getAllEntries = function (req, res) {
    Entry.find().exec(function (err, result) {
        if (err) {
            console.log(err);
            res.status(500).type('text').send({msg: 'DB Error', res: false});
        } else {
            if (result.length > 0) {
                console.log(result);
                res.status(200).type('application/json').send(result);
            }
            else {
                res.status(200).type('application/json').send({msg: 'No Entries found', res: false});
            }
        }
    })
};

//Bestimmten Entry anzeigen
module.exports.getEntryById = function (req, res) {
    console.info(req.params.id);
    Entry.findById(req.params.id, function (err, result) {
        if (err) {
            res.status(500).type('text').write({msg: 'DB Error', res: false});
        } else {
            if (result != null) {
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
            res.status(500).type('text').write({msg: 'DB Error', res: false});
        } else {
            if (result != null) {
                res.status(200).type('application/json').send({
                    msg: 'Entry with id: ' + result._id + ' successfully updated',
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

//Wenn daten analysiert und Tutorial erstellt, wird die @tutorial_id im Entry aktualisiert
module.exports.updateEntryTutorialId = function (id, tutorial_id) {
    Entry.findById(id, function (err, result) {
        console.info(result);
        if (err) {
            console.error("DB error: " + err);
        } else {
            if (result) {
                result.tutorial_id = tutorial_id;
            } else {
                console.error('Entry with id: ' + id + ' not found');
            }
            result.save(function (err) {
                if (err) {
                    console.error('Entry not updated :' + err);
                } else {
                    console.log('Entry updated with tutorial_id: ' + tutorial_id);
                }
            });
        }

    });
};

module.exports.deleteEntry = function (req, res) {
    Entry.findByIdAndRemove(req.params.id, function (err, result) {
        console.info(result);
        if (err) {
            res.status(500).type('application/json').write({msg: "DB error: " + err, res: false});
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
