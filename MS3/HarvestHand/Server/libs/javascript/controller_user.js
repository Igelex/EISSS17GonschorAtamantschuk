/**
 * Created by Pastuh on 02.05.2017.
 */
var User = require('../models_mongoose/user');

//Neuen User anlegen
module.exports.registerUser = function (req, res) {
    //Überprüfen ob User bereits existiert
    User.findOne({phone_number: req.body.phone_number}, function (err, result) {
        if (err) {
            res.status(500).type('application/json').send({msg: 'DB Error'});
            console.error(err);
            return;
        }
        if (result) {
            //Es existiert bereits ein user mit der angegeben Email existiert
            console.log('User already exists');
            res.status(409).type('application/json').send({msg: 'User already exists', res: false});

        } else {
            //Es existiert kein user mit der angegeben Email
            var newUser = new User(req.body);
            //User Speichern
            newUser.save(function (err, result) {
                if (err) {
                    //Fehler bei der Datenbankabfrage
                    res.status(500).send({msg: 'DB Error'});
                    console.error(err);
                }
                console.error(result);
                res.status(200).type('application/json').send(result);
            });
        }
    });
};
//Einlogen im System
module.exports.logIn = function (req, res) {
    console.log('Params Number ' + req.body.phone_number);
    User.findOne({'phone_number': req.body.phone_number}, function (err, user) {
        if (err) {
            res.status(500).type('text').send("DB error: " + err);
        }
        if (user) {
            //Erfolgreich eingeloggt
            console.log('Welcome: ' + user);
            res.status(200).type('application/json').send(user);
        } else {
            console.log('Incorrect NUmber :' + user);
            //User nicht autorisiert
            res.status(401).type('application/json').send({msg: 'Incorrect Number', res: true});
        }
    });

};

//Daten des Bestimmten User anzeigen
module.exports.getUserById = function (req, res) {
    console.info('request user for login with id: ' + req.params.id);
    User.findById(req.params.id, function (err, result) {
        if (err) {
            res.status(500).send("DB error: " + err);
        } else {
            if (result) {
                var currentUser = {
                    user_id: result._id,
                    user_type: result.user_type,
                    phone_number: result.phone_number
                };
                console.log('In get User by Id: ' + currentUser);
                res.status(200).type('application/json').send(currentUser);
            }
            else {
                console.log('No user found: ' + result);
                /*Status 204 macht hier mehr Sinn(no content), Androids Volley Framework interpretiert aber status 204 und als
                Time out error*/
                res.status(200).type('application/json').send({});
            }
        }
    });
};

//Daten aktualisieren
module.exports.updateUser = function (req, res) {
    User.findOne({phone_number: req.body.phone_number}, function (err, result) {
        if (err) {
            res.status(500).type('application/json').send({msg: 'DB Error'});
            console.error(err);
            return;
        }
        if (result) {
            //Es existiert bereits ein User mit der angegeben Nummer
            console.log('User already exists');
            res.status(409).type('application/json').send({msg: 'User already exists', res: false});

        } else {
            User.findByIdAndUpdate(req.params.id, {phone_number: req.body.phone_number}, function (err, result) {
                if (err) {
                    res.status(500).type('text').send("DB error: " + err);
                } else {
                    if (result) {
                        //@res gibt Auskunft darüber, ob die Aktion erfolgreich war
                        res.status(200).type('application/json').send({
                            msg: 'User with id: ' + result._id + ' successful updated',
                            res: true
                        });
                    } else {
                        res.status(200).type('application/json').send({
                            msg: 'User with id: ' + req.params.id + ' not found',
                            res: false
                        });
                    }
                }

            });

        }
    });
};

module.exports.deleteUser = function (req, res) {
    User.findByIdAndRemove(req.params.id, function (err, result) {
        console.info(result);
        if (err) {
            res.status(500).type('text').write("DB error: " + err);
        } else {
            if (result) {
                //@res gibt Auskunft darüber, ob die Aktion erfolgreich war
                res.status(200).type('application/json').send({
                    msg: 'User with id: ' + result._id + ' successful deleted',
                    res: true
                });
            } else {
                //@res gibt Auskunft darüber, ob die Aktion erfolgreich war
                res.status(200).type('text').send({
                    msg: 'User with id: ' + req.params.id + ' not found',
                    res: false
                });
            }
        }
    });
};
//User wird nach der Telefonnummer gesucht und als Collaborator hinzugefügt
module.exports.getUsers = function (req, res) {
    User.findOne({'phone_number': req.query.phone_number},
        {'_id': true}, function (err, result) {
            if (err) {
                res.status(500).send("DB error: " + err);
            } else {
                if (result) {
                    console.log('Get Collab: ' + result);
                    //@res gibt Auskunft darüber, ob die Aktion erfolgreich war
                    res.status(200).send({
                        collab_id: result._id,
                        res: true
                    });
                } else {
                    console.log('No Collab: ' + result);
                    res.status(200).type('text').send({
                        msg: 'User with number: ' + req.params.id + ' not found',
                        res: false
                    });
                }
            }

        })
};
