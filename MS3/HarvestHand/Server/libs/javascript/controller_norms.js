/**
 * Created by Pastuh on 02.05.2017.
 */
var Norm = require('../models_mongoose/norms'),
    fs = require('fs');

//Norm Daten werden eingelesen und in der DB gespeichert
module.exports.addNorm = function () {
    console.log('Read Norms file ...');
    var norms = JSON.parse(fs.readFileSync('crops_norms.json', 'utf8'));
    console.log('Read result: ' + norms[1].name);
    for (var i in norms){
        var newNorm = new Norm(norms[i]);
        newNorm.save(function (err, result) {
            if (err) {
                console.error(err);
                //res.status(500).type('text').send('Norm not saved :' + err);
            } else {
                console.log('Norms saved ' + result);
                //res.status(200).type('text').send(result);
            }
        });
    }
};

module.exports.getNormById = function (req, res) {
    console.info('In getNormById: ' + req.params.id);
    Norm.findOne({art_id: req.params.id}, function (err, result) {
        if (err) {
            res.status(500).type('text').send('DB error :' + err);
        } else {
            console.log('NORMNORM : ' + result);
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
            if (result != null) {
                res.status(200).type('text').send('Norm with id: ' + result._id + ' successfully deleted');
            } else {
                res.status(200).type('text').send('Norm with id: ' + req.params.id + ' not found');
            }
        }

    });
};

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
                res.status(200).type('application/json').send(result);
            }
            else {
                res.status(200).type('text').send('No Norms found');
            }
        }
    })
};
