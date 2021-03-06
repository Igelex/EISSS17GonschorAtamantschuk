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

/**
 * Es werden alle Entries Angezeigt, in denen ein User als Owner und als Collaborator eingetragen ist
 * @param req
 * @param res
 */
module.exports.getEntries = function (req, res) {
    console.log("Get Entries owner_id: " + req.query.owner_id);
    console.log("Get Entries collab_id: " + req.query.collab_id);
    Entry.find({
            $or: [{owner_id: req.query.owner_id}, {collaborators_id: req.query.collab_id}]
        },
        {
            //Projektion
            "entry_name": true,
            "art_id": true,
            "area": true,
            "location": true,
            "tutorial_id": true,
            "crop_id": true
        }, function (err, result) {
            if (err) {
                console.log(err);
                res.status(500).type('text').send({msg: 'DB Error', res: false});
            } else {
                if (result.length > 0) {
                    console.log("Get Entries ====================================== " );
                    res.status(200).type('application/json').send(result);
                }
                else {
                    /*Status 204 macht hier mehr Sinn(no content), Androids Volley Framework interpretiert aber status 204 und als
                     Time out error*/
                    res.status(200).type('application/json').send([]);
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
                res.status(200).type('application/json').send();
            }
        }
    });
};

//Wenn daten analysiert und Tutorial erstellt, wird die @tutorial_id des Entrys aktualisiert
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
/**
 * Daten Aktualisieren
 */
module.exports.updateEntry = function (req, res) {
    console.error('IN update Entry...........');
    Entry.findByIdAndUpdate(req.params.id, req.body, function (err, result) {
        if (err) {
            console.error("DB error: " + err);
            res.status(500).type('application/json').send({msg: "DB error: " + err, res: false});
        } else {
            if (result) {
                //Erst wird das Tutorial gelöscht
                controller_tutorial.deleteTutorial(result.tutorial_id);

                console.info("Ready to analyze... ");
                //Die Bodenanalyse wird mit den aktualisierten Daten durchgeführt
                analyzer.analyseData(req.body);

                //@res gibt Auskunft darüber, ob die Aktion erfolgreich war
                res.status(200).type('application/json').send({
                    msg: 'Entry with id: ' + result._id + ' updated',
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
/**
 * Eintrag löschen
 * @param req
 * @param res
 */
module.exports.deleteEntry = function (req, res) {
    Entry.findByIdAndRemove(req.params.id, function (err, result) {
        if (err) {
            res.status(500).type('application/json').send({msg: "DB error: " + err, res: false});
        } else {
            if (result) {
                //zugehöriges Tutorial wird ebenfalls gelöscht
                controller_tutorial.deleteTutorial(result.tutorial_id);
                //@res gibt Auskunft darüber, ob die Aktion erfolgreich war
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
