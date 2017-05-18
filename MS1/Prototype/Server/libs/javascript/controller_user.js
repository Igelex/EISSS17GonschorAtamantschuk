/**
 * Created by Pastuh on 02.05.2017.
 */
var User = require('../models_mongoose/user');

//Neuen User anlegen
module.exports.registerUser = function (req, res) {

    //Überprüfen ob User bereits existiert
    User.findOne({email: req.body.email}, function (err, result) {
        if (err) {
            res.status(500).type('application/json').send({error_msg: 'DB Error'});
            console.error(err);
            return;
        }

        if (result) {
            //Es existiert bereits ein user mit der angegeben Email existiert
            console.log('User already exists');
            res.status(409).send({error_msg: 'User already exists'});

        } else {
            //Es existiert kein user mit der angegeben Email
            var newUser = new User({
                first_name: req.body.first_name,
                last_name: req.body.last_name,
                gender: req.body.gender,
                email: req.body.email,
                pass: req.body.pass,
                user_type: req.body.user_type
            });

            //User Speichern
            newUser.save(function (err, result) {

                if (err) {
                    //Fehler bei der Datenbankabfrage
                    res.status(500).send({error_msg: 'DB Error'});
                    console.error(err);
                    return;
                }
                console.error(result);
                res.status(200).send({success_msg: 'Registration sucessfull'});
            });
        }
    });
};

module.exports.validPassword = function (pass, user_id) {
    console.log('pass: ' + pass);
    console.log('In valid id: ' + user_id);
    User.findById(user_id, function (err, result) {
        if (err) {
            console.log(err);
        } else {
            if (result) {
                console.log('In valid: ' + result.pass);
                if (pass == result.pass) {
                    return true;
                } else {
                    return false;
                }
            }
            else {
                return false;
            }
        }
    });
};

/*//Alle Entries anzeigen
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

 //Bestimmten Entry anzeigen
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

 //Daten aktualisieren
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

 //Wenn daten analysiert und Tutorial erstellt, wird die @tutorial_id im Entry aktualisiert
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
 res.status(500).type('text').write("DB error: " + err);
 } else {
 if (result != null) {
 res.status(200).type('text').send('Entry with id: ' + result._id + ' succesfully deleted');
 } else {
 res.status(200).type('text').send('Entry with id: ' + req.params.id + ' not found');
 }
 }

 });
 };*/
