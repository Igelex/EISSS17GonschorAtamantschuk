/**
 * Created by Pastuh on 19.05.2017.
 */
var express = require('express');
var router = express.Router();

router.get('/users/signinerror', function(req, res){
    var errmsg = req.flash('error');
    res.status(200).type('application/json').send({error_msg: errmsg });
});

module.exports = router;