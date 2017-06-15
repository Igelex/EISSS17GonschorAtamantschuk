
/**
 * Created by franz on 15.06.2017.
 */

var request = require('request');
var Key = '28182cd556dbb993';
var requestUrl = 'http://api.wunderground.com/api/28182cd556dbb993/geolookup/q/autoip.json';

module.exports.location = function (req,res) {
    request(requestUrl, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            var result = JSON.parse(body);
            console.log(result);
        } else {
            console.log(error, response.statusCode, body);
        }
        res.send(result);
    });
};
