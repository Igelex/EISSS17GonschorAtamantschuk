/**
 * Created by Pastuh on 02.05.2017.
 */
var User = require('../models_mongoose/user');

//Neuen User anlegen
module.exports.registerUser = function (req, res) {

    //Überprüfen ob User bereits existiert
    User.findOne({email: req.body.email}, function (err, result) {
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
            var newUser = new User({
                name: req.body.name,
                email: req.body.email,
                pass: req.body.pass,
                user_type: req.body.user_type,
                phone_number: req.body.phone_number
            });

            //User Speichern
            newUser.save(function (err, result) {

                if (err) {
                    //Fehler bei der Datenbankabfrage
                    res.status(500).send({msg: 'DB Error'});
                    console.error(err);
                    return;
                }
                console.error(result);
                res.status(200).type('application/json').send(result);
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
                console.log('In get User by Id: ' + result);
                res.status(200).type('application/json').send(result);
            }
            else {
                console.log('In get User by , result 0: ' + result);
                res.status(204).type('application/json').send({msg: 'No User found', res: false});
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
    User.find(function (err, result) {
        if (err){
            res.status(500).send("DB error: " + err);
        }else {
            if (result){
                res.status(200).send(result);
            }else {
                res.status(204).send("No users found");
            }
        }

    })


}
