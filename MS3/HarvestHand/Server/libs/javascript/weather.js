/**
 * Created by franz on 24.05.2017.
 */

var request = require('request');
var key = '28182cd556dbb993';

function  getPrecipitationForWeek(location) {
    var requestUrl = 'http://api.wunderground.com/api/' + key + '/forecast10day/q/DE/Gummersbach.json'
    var requestUrl = 'http://api.wunderground.com/api/' + key + /forecast10day/q/zmw:94125.1.99999.json'
}

//var requestUrl = 'http://api.wunderground.com/api/' + key + '/geolookup/q/+ location'
var requestUrl = 'http://api.wunderground.com/api/' + key + '/conditions/q/DE/Gummersbach.json'
module.exports.wetter = function (req, res) {
    request(requestUrl, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            var result = JSON.parse(body);
            console.log(result.current_observation.display_location.full);
            console.log(result.current_observation.display_location.country);
            console.log(result.current_observation.observation_time);
            console.log(result.current_observation.local_time_rfc822);
            console.log(result.current_observation.weather);
            console.log(result.current_observation.temperature_string);
            console.log(result.current_observation.temp_f);
            console.log(result.current_observation.temp_c);
            console.log(result.current_observation.relative_humidity);
            console.log(result.current_observation.wind_string);
            console.log(result.current_observation.pressure_mb);
            console.log(result.current_observation.dewpoint_string);
            console.log(result.current_observation.dewpoint_f);
            console.log(result.current_observation.dewpoint_c);
            console.log(result.current_observation.heat_index_string);
            console.log(result.current_observation.heat_index_f);
            console.log(result.current_observation.heat_index_c);
            console.log(result.current_observation.windchill_string);
            console.log(result.current_observation.feelslike_string);
            console.log(result.current_observation.feelslike_f);
            console.log(result.current_observation.feelslike_c);
            console.log(result.current_observation.precip_today_string);
            console.log(result.current_observation.precip_today_in);
            console.log(result.current_observation.precip_today_metric);
            console.log(result.current_observation.icon);
            console.log(result.current_observation.icon_url);
            console.log(result.current_observation.forecast_url);
            console.log(result.current_observation.history_url);
            console.log(result.current_observation.ob_url);

        } else {
            console.log(error, response.statusCode, body);
        }
        res.send(result);
    });
};
