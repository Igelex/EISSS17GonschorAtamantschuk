/**
 * Created by Pastuh on 02.05.2017.
 */
var Norm = require('../models_mongoose/norms');

module.exports.addNorm = function (req, res) {
    var newNorm = new Norm({
        norm_name: req.body.norm_name,
        art_id: req.body.art_id,
        ph_norm: req.body.ph_norm,
        water_norm: req.body.water_norm,
        minerals_norm: req.body.minerals_norm
    });
    newNorm.save(function (err, result) {
        if (err) {
            console.log(err);
            res.status(500).type('text').send('Norm not saved :' + err);
        } else {
            console.log('Norm saved');
            res.status(200).type('text').send('Norm saved :' + result);
        }
    });
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
                res.status(200).type('text').send('Norm with id: ' + result._id + ' succesfully deleted');
            } else {
                res.status(200).type('text').send('Norm with id: ' + req.params.id + ' not found');
            }
        }

    });
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
