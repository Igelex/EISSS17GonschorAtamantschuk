/**
 * Created by Pastuh on 02.05.2017.
 */
var User = require('../models_mongoose/user');

//Neuen User anlegen
module.exports.registerUser = function (req, res) {

    //Überprüfen ob User bereits existiert
    User.findOne({email: req.body.phone_number}, function (err, result) {
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
                    return;
                }
                console.error(result);
                res.status(200).type('application/json').send({_id: result._id, user_type: result.user_type});
            });
        }
    });
};

//Bestimmten User anzeigen
module.exports.getUserById = function (req, res) {
    console.info(req.params.id);
    User.findById(req.params.id, function (err, result) {
        if (err) {
            res.status(500).type('text').send("DB error: " + err);
        } else {
            if (result) {
                var currentUser = {
                    user_id: result._id,
                    name: result.name,
                    user_type: result.user_type,
                    pass: result.pass,
                    phone_number: result.phone_number
                };
                console.log('In get User by Id: ' + currentUser);
                res.status(200).type('application/json').send(currentUser);
            }
            else {
                console.log('No user found: ' + result);
                res.status(204).type('application/json').send();
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
                res.status(200).type('application/json').send({msg: 'User with id: ' + result._id + ' successfully updated', res: true});
            } else {
                res.status(204).type('application/json').send({msg: 'User with id: ' + req.params.id + ' not found', res: false});
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
                res.status(200).type('application/json').send({msg: 'User with id: ' + result._id + ' successfully deleted', res: true});
            } else {
                res.status(204).type('text').send({msg: 'User with id: ' + req.params.id + ' not found', res: false});
            }
        }
    });
};

module.exports.getUsers = function (req, res){
    User.findOne({'phone_number': req.query.phone_number},
        {'_id' : true},function (err, result) {
        if (err){
            res.status(500).send("DB error: " + err);
        }else {
            if (result){
                console.log('Get Collab: ' + result);
                res.status(200).send(result);
            }else {
                console.log('No Collab: ' + result);
                res.status(204).send();
            }
        }

    })
};
