/**
 * Created by franz on 24.05.2017.
 */

var request = require('request');
var Key = '28182cd556dbb993';

var requestUrl = 'http://api.wunderground.com/api/28182cd556dbb993/conditions/q/CA/San_Francisco.json'
module.exports.wetter = function (req, res) {
    request(requestUrl, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            var result = JSON.parse(body);
            console.log(result.current_observation.display_location.city);
        } else {
            console.log(error, response.statusCode, body);
        }
        res.send(result);
    });
};