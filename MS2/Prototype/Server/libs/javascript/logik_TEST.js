/*
var NORM = 0, //flag, wenn daten von der Norm nicht abweichen
    LESS = 1, //flag, wenn daten kleiner als die Norm
    GREATER = 2, //flag, wenn daten größer als die Norm
    status = 0;

function analyseData(entry) {
    /!*Hol Norm aus der DAtenbank zur jeweiligen Pflanze*!/
    var externalRequest = http.request(){
     var norm = data;
    }

    request("URL zum Hollen der Niederschlagsdaten für eine Woche"){
        var wochenNiederschlag = response;
    }

    /!*Füre anylyse für alle Eigenschaften*!/
    forEach(entry.eingenschaft){
        /!*Vergleiche Entry.daten mit Norm.daten*!/

        if(entry.soil_moisture <= norm.soil_moisture.min){
            /!*Berechne abweichnung in %*!/
            var deviation = entry.soil_moisture ist Prozent von norm.soil_moisture.min;
            /!*Berechne benötigte Wassermenge für eine Woche*!/
            var water_requirements = norm.water_requirements * entry.area - wochenNiederschlag;
            status = LESS;
        }else if (entry.soil_moisture >= norm.soil_moisture.max){
            /!*Berechne abweichnung in %*!/
            var deviation = norm.soil_moisture.max ist Prozent von entry.soil_moisture;
            status = GREATER;
        }else{
            /!*Berechne abweichnung in %*!/
            var deviation = entry.soil_moisture ist Prozent von norm.soil_moisture.min;
            status = NORM;
        }
        /!*
        .
        .
        .
        *!/
        if(entry.air_temp <= norm.air_temp.min){
            /!*Berechne abweichnung in %*!/
            var deviation = entry.air_temp ist Prozent von norm.air_temp.min;
            status = LESS;
        }else if (entry.soil_moisture >= norm.air_temp.max) {
            /!*Berechne abweichnung in %*!/
            var deviation = norm.air_temp.max ist Prozent  von entry.air_temp;
            status = GREATER;
        }
        else{
            /!*Berechne abweichnung in %*!/
            var deviation = entry.air_temp ist Prozent von air_temp.min;
            status = NORM;
        }
        /!*
         .
         .
         .
         *!/
    }

    function saveTutorialInDb(alleDaten);

}*/
