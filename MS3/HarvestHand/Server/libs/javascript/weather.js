/**
 * Created by franz on 24.05.2017.
 */

var request = require('request'),
    analyzer = require('./analyzer'),
    key = '28182cd556dbb993';
/**
 * Wochentliche Niederschlagsmenge wird von Weather Underground requested
 * @param countryISOCode - DE, SN,...
 * @param city - Stadt
 */
module.exports.getPrecipitationForWeek = function (countryISOCode, city) {
    var weekPrecipitation = 0;
    //Die Request-Url
    var requestUrl = 'http://api.wunderground.com/api/' + key + '/forecast10day/q/' + countryISOCode + '/'
        + city + '.json';
    console.log('Send request...');
    //Sende Request
    request(requestUrl, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            if (response) {
                var result = JSON.parse(body);
                try {
                    //Die Vorhersagedaten werden aus der Response rausgenommen
                    var forecastArray = result.forecast.simpleforecast.forecastday;
                    //Die Response liefert 10 Tage-Vohrhersage, gebracuht werden nur 7
                    for (var i = 0; i < 7; i++) {
                        if (forecastArray[i].hasOwnProperty()) continue;
                        console.log(forecastArray[i].qpf_allday.mm);
                        //Niederschlag für 7 Tage wierd zusammenaddiert
                        weekPrecipitation += forecastArray[i].qpf_allday.mm;
                    }
                    /**
                     * 1 mm Niederschlag == 1 Liter/m^2
                     * */
                    console.log('Precipitation week : ' + weekPrecipitation);
                    //Die Bodenanalyse wird weiter durchgeführt
                    analyzer.analyseValues(weekPrecipitation);
                }catch (e){
                    console.error("JSON ERROR in Precipitation: " + e);
                } finally {
                    //Auch im Fall, dass keine Wetterdane vorhanden, sollte die Bodenanalyse weiter durchgeführt werden
                    analyzer.analyseValues(weekPrecipitation);
                }
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
 * Request Wetterdaten (Temperatur und Luftfeuchtigkeit nach Location). Wird zur Autofillfunktion auf dem Client benutzt
 * @param req
 * @param res
 */
module.exports.getAirTemp = function (req, res) {
    console.log("AIR temp ISO: " + req.query.countryISOCode);
    console.log("AIR temp city: " + req.query.city);

    /*Leerzeichen in dem Stadtnamen wird durch '_' Zeichen ersetzt, ist API spezifisch */
    var city = req.query.city.replace(/ /g, "_");
    //Request URL
    var url = 'http://api.wunderground.com/api/' + key + '/conditions/q/' + req.query.countryISOCode
        + '/' + city + '.json';

    request(url, function (err, response, body) {
        if (err){
            res.status(500).send(err);
        }
        if (response.statusCode == 200) {
            var result = JSON.parse(body);
            try {
                var hymidity = result.current_observation.relative_humidity;
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
