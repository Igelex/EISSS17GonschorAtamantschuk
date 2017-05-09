/**
 * Created by Pastuh on 02.05.2017.
 */
var Users = require('../models_mongoose/users');

module.exports.addUser = function(req, res) {
    var newUser = new Users({
        first_name: req.body.first_name,
        last_name: req.body.last_name
        /*gender: req.body.gender,
        birth_year: req.body.birth_year,
        email: req.body.email,
        pass: req.body.pass,
        picture: req.body.picture,
        type: req.body.type*/
    });
    newUser.save(function (err, result) {
        if (err) {
            console.log(err);
            res.status(500).type('text').send('User not saved :' + err);
        } else {
            console.log('user saved');
            res.status(200).type('text').send('User saved :' + newUser);
        }
    });
};
