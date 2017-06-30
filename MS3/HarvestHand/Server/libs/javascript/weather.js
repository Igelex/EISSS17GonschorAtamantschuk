/**
 * Created by franz on 24.05.2017.
 */

var request = require('request'),
    analyzer = require('./analyzer'),
    key = '28182cd556dbb993';

/*var requestUrl2 = 'http://api.wunderground.com/api/' + key + /forecast10day/q/zmw:94125.1.99999.json'
 var requestUrl1 = 'http://api.wunderground.com/api/' + key + '/forecast10day/q/DE/Gummersbach.json'*/

module.exports.getPrecipitationForWeek = function (countryISOCode, city) {
    var weekPrecipitation = 0;
    var requestUrl = 'http://api.wunderground.com/api/' + key + '/forecast10day/q/' + countryISOCode + '/'
        + city + '.json';
    console.log('Send request...');
    request(requestUrl, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            if (response) {
                console.log('ON Response...');
                var result = JSON.parse(body);
                var forecastArray = result.forecast.simpleforecast.forecastday;

                for (var i = 0; i < 7; i++) {
                    if (forecastArray[i].hasOwnProperty()) continue;
                    console.log(forecastArray[i].qpf_allday.mm);
                    weekPrecipitation += forecastArray[i].qpf_allday.mm;
                }
                /**
                 * 1 mm Niederschlag == 1 Liter/m^2
                 * */
                console.log('Precipitation week : ' + weekPrecipitation);
                analyzer.analyseValues(weekPrecipitation);

            } else {
                console.log('No weatherdata : ' + response);
                analyzer.analyseValues(weekPrecipitation);
            }
        } else {
            console.log(error, response.statusCode, body);
            analyzer.analyseValues(weekPrecipitation);
        }
    });
};
/**
 * Request Wetterdaten (Temperatur und Luftfeuchtigkeit nach Location.
 * @param req
 * @param res
 */
module.exports.getAirTemp = function (req, res) {
    console.log("AIR temp params: " + req.params);
    console.log("AIR temp query params: " + req.query.params);
    console.log("AIR temp query: " + req.query.city);
    var url = 'http://api.wunderground.com/api/' + key + '/conditions/q/' + req.query.countryISOCode
        + '/' + req.query.city + '.json';
    console.log('Air temp URL : ' + url);
    request(url, function (err, response, body) {
        console.log('Air temp status : ' + response.status);
        if (err){
            res.status(500).send(err);
        }
        if (response.statusCode == 200) {
            var result = JSON.parse(body);
            //console.log('Air temp body : ' + body);
            try {
                var hymidity = result.current_observation.relative_humidity;
                console.log('Humidyty befor: ' + hymidity);
                console.log('Hymidity after: ' + hymidity.slice(-hymidity.length,-1));
                var clima ={
                    temp: result.current_observation.temp_c,
                    //Feuchtigkeit kommt als String, %-Zeichen wird abgetrennt und String zu Int konvertiert(70% -> 70)
                    humidity: parseInt(hymidity.slice(-hymidity.length,-1))
                };
            } catch (e){
                console.error("JSON ERROR: " + e);
            }

            res.status(200).send(clima);
        } else {
            res.status(response.statusCode).send();
        }
    })
};
