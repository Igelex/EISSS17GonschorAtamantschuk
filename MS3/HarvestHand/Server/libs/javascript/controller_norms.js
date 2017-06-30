/**
 * Created by Pastuh on 02.05.2017.
 */
var Norm = require('../models_mongoose/norms'),
    fs = require('fs');

//Norm Daten werden eingelesen und in der DB gespeichert
module.exports.addNorm = function () {
    fs.readFile('crops_norms.json', 'utf8', function (err, data) {
        if (err){
            console.error('Error readFile: ' + err);
        }
        var norms = JSON.parse(data);
        for (var i in norms){
            var newNorm = new Norm(norms[i]);
            newNorm.save(function (err, result) {
                if (err) {
                    console.error('Error, already exist');
                } else {
                    console.log('Norm saved: ' + result[1].name);
                }
            });
        }
    });
};

module.exports.getNormById = function (req, res) {
    Norm.findOne({art_id: req.params.id}, function (err, result) {
        if (err) {
            res.status(500).type('text').send('DB error :' + err);
        } else {
            res.status(200).type('text').send(result);
        }
    });
};

module.exports.deleteNorm = function (req, res) {
    Norm.findByIdAndRemove(req.params.id, function (err, result) {
        console.info(result);
        if (err) {
            res.status(500).type('text').write("DB error: " + err);
        } else {
            if (result) {
                res.status(200).type('text').send('Norm with id: ' + result._id + ' successful deleted');
            } else {
                res.status(200).type('text').send('Norm with id: ' + req.params.id + ' not found');
            }
        }

    });
};
//For debugging
module.exports.getNorm = function (crop_id) {
    Norm.findOne({
            crop_id: crop_id
        },
        function (err, result) {
            if (err) {
                console.log("GetNorm DB Error:" + err);
            } else {
                if (result) {
                    console.log("NORM is: " + result);
                    return result;
                }
                else {
                    console.log("No Norm found");
                    return null;
                }
            }

        })
};

//For debugging
module.exports.getAllNorms = function (req, res) {
    Norm.find().exec(function (err, result) {
        if (err) {
            console.log(err);
            res.status(500).type('text').send('DB error :' + err);
        } else {
            if (Object.keys(result).length > 0) {
                res.status(200).send(result);
            }
            else {
                res.status(200).send('No Norms found');
            }
        }
    })
};
