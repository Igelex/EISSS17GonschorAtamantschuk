/**
 * Created by franz on 24.05.2017.
 */

var request = require('request'),
    analyzer = require('./analyzer'),
    key = '28182cd556dbb993';

/*var requestUrl2 = 'http://api.wunderground.com/api/' + key + /forecast10day/q/zmw:94125.1.99999.json'
 var requestUrl1 = 'http://api.wunderground.com/api/' + key + '/forecast10day/q/DE/Gummersbach.json'*/

module.exports.getPrecipitationForWeek = function (country, city) {
    var weekPrecipitation = 0;
    country = 'SN';
    city = 'Dakar'

    var requestUrl = 'http://api.wunderground.com/api/' + key + '/forecast10day/q/' + country + '/'
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
                /*
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
