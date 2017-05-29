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
            console.log(result.current_observation.display_location.full);
            console.log(result.current_observation.display_location.country);
            console.log(result.current_observation.estimated.observation_time);
            console.log(result.current_observation.estimated.local_time_rfc822);
            console.log(result.current_observation.estimated.weather);
            console.log(result.current_observation.estimated.temperature_string);
            console.log(result.current_observation.estimated.temp_f);
            console.log(result.current_observation.estimated.temp_c);
            console.log(result.current_observation.estimated.relative_humidity);
            console.log(result.current_observation.estimated.wind_string);
            console.log(result.current_observation.estimated.pressure_mb);
            console.log(result.current_observation.estimated.dewpoint_string);
            console.log(result.current_observation.estimated.dewpoint_f);
            console.log(result.current_observation.estimated.dewpoint_c);
            console.log(result.current_observation.estimated.heat_index_string);
            console.log(result.current_observation.estimated.heat_index_f);
            console.log(result.current_observation.estimated.heat_index_c);
            console.log(result.current_observation.estimated.windchill_string);
            console.log(result.current_observation.estimated.feelslike_string);
            console.log(result.current_observation.estimated.feelslike_f);
            console.log(result.current_observation.estimated.feelslike_c);
            console.log(result.current_observation.estimated.precip_today_string);
            console.log(result.current_observation.estimated.precip_today_in);
            console.log(result.current_observation.estimated.precip_today_metric);
            console.log(result.current_observation.estimated.icon);
            console.log(result.current_observation.estimated.icon_url);
            console.log(result.current_observation.estimated.forecast_url);
            console.log(result.current_observation.history_url);
            console.log(result.current_observation.ob_url);

        } else {
            console.log(error, response.statusCode, body);
        }
        res.send(result);
    });
};