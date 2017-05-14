/**
 * Created by Pastuh on 02.05.2017.
 */
var Entry = require('../models_mongoose/entry'),
    analyzer = require('./analyzer'),
    controller_tutorial = require('./controller_tutorial');

module.exports.addEntry = function (req, res) {
    var newEntry = new Entry({
        entry_name: req.body.entry_name,
        art_id: req.body.art_id,
        tutorial_id: req.body.tutorial_id,
        collaborators:req.body.collaborators,
        ph_value: req.body.ph_value,
        water: req.body.water,
        minerals: req.body.minerals
    });
    newEntry.save(function (err, result) {
        if (err) {
            console.log(err);
            res.status(500).type('text').send('Entry not saved :' + err);
        } else {
            console.log('Entry saved');
            res.status(200).type('text').send('Entry saved :' + result);
            analyzer.analyseData(result);
        }
    });
};

module.exports.getAllEntries = function (req, res) {
    Entry.find().exec(function (err, result) {
        if (err) {
            console.log(err);
            res.status(500).type('text').send('DB error :' + err);
        } else {
            if (Object.keys(result).length > 0) {
                res.status(200).type('application/json').send(result);
            }
            else {
                res.status(200).type('text').send('No Entries found');
            }
        }
    })
};

module.exports.getEntryById = function (req, res) {
    console.info(req.params.id);
    Entry.findById(req.params.id, function (err, result) {
        if (err) {
            res.status(500).type('text').write("DB error: " + err);
        } else {
            if (result != null) {
                res.status(200).type('application/json').send(result);
            }
            else {
                res.status(200).type('text').send('No Entry found');
            }
        }
    });
};

module.exports.updateEntry = function (req, res) {
    Entry.findByIdAndUpdate(req.params.id, req.body, function (err, result) {
        console.info(result);
        if (err) {
            res.status(500).type('text').write("DB error: " + err);
        } else {
            if (result != null) {
                res.status(200).type('text').send('Entry with id: ' + result._id + ' successfully updated');
            } else {
                res.status(200).type('text').send('Entry with id: ' + req.params.id + ' not found');
            }
        }

    });
};

module.exports.updateEntryTutorialId = function (id, tutorial_id) {
    Entry.findById(id, function (err, result) {
        console.info(result);
        if (err) {
            res.status(500).type('text').write("DB error: " + err);
        } else {
            if (result != null) {
                result.tutorial_id = tutorial_id;
            } else {
                res.status(200).type('text').send('Entry with id: ' + id + ' not found');
            }

            result.save(function (err) {
                if (err){
                    console.error('Entry not updated :' + err);
                }else{
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
            res.status(500).type('text').write("DB error: " + err);
        } else {
            if (result != null) {
                res.status(200).type('text').send('Entry with id: ' + result._id + ' succesfully deleted');
            } else {
                res.status(200).type('text').send('Entry with id: ' + req.params.id + ' not found');
            }
        }

    });
};

//Eintrag inklusive zugehöriges Tutorial werden an client geschickt
module.exports.getEntryTutorial = function (req, res) {
    Entry.findById(req.params.id, function (err, result_entry) {
        console.log('result_entra: ' + result_entry);
        if (err) {
            res.status(500).type('text').write("DB error: " + err);
        } else {
            if (result_entry != null) {
                controller_tutorial.getTutorialById(req, res, result_entry);
            }
            else {
                res.status(200).type('text').send('No Entry found');
            }
        }
    });

}
