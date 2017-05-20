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
            res.status(409).type('application/json').send({error_msg: 'User already exists'});

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
                res.status(200).type('application/json').send({success_msg: 'Registration sucessfull'});
            });
        }
    });
};

//Bestimmten User anzeigen
module.exports.getUserById = function (req, res) {
    console.info(req.params.id);
    User.findById(req.params.id, function (err, result) {
        if (err) {
            res.status(500).type('text').write("DB error: " + err);
        } else {
            if (result) {
                res.status(200).type('application/json').send(result);
            }
            else {
                res.status(204).type('application/json').send({error_msg: 'No User found'});
            }
        }
    });
};

//Daten aktualisieren
module.exports.updateUser = function (req, res) {
    User.findByIdAndUpdate(req.params.id, req.body, function (err, result) {
        console.info(result);
        if (err) {
            res.status(500).type('text').write("DB error: " + err);
        } else {
            if (result) {
                res.status(200).type('application/json').send({success_msg: 'User with id: ' + result._id + ' successfully updated'});
            } else {
                res.status(204).type('application/json').send({error_msg: 'User with id: ' + req.params.id + ' not found'});
            }
        }

    });
};

module.exports.deleteUser = function (req, res) {
    User.findByIdAndRemove(req.params.id, function (err, result) {
        console.info(result);
        if (err) {
            res.status(500).type('text').write("DB error: " + err);
        } else {
            if (result != null) {
                res.status(200).type('application/json').send({success_msg: 'User with id: ' + result._id + ' succesfully deleted'});
            } else {
                res.status(204).type('text').send({error_msg: 'User with id: ' + req.params.id + ' not found'});
            }
        }
    });
};
