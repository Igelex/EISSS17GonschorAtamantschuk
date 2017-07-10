/**
 * Created by franz on 04.07.2017.
 */

var websms = require('websmscom');
var username = 'franziska.gonschor@web.de';
var password = 'theisss17';
var gatewayUrl =  'oms:https://gateway.sms.at/oms/?UserID=franziska.gonschor@web.de';
var recipientAddressList = ['4917696217295'];
var unicodeMessageText   = 'Test';
var maxSmsPerMessage     = 1;
var isTest               = true; // true: do not send sms but test interface
var request = require('request');

function main(){
    var myClient = new websms.Client(gatewayUrl, username, password);
    var url = 'https://franziska.gonschor@web.de:theisss17@api.websms.com/rest/smsmessaging/simple?recipientAddressList=4917696217295&messageContent=test&test=true';

    request(url, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            if (response) {
                var result = JSON.parse(body);
                try {
                    console.log(result);
                } catch (e) {
                    console.error('Error' + e);
                }
            } else {
                console.log('No smsdata : ' + response);
            }
        }
       else {
            console.log(error, response.statusCode, body);
       }

    })}
    main()


        /*  http.request(url, function (res) {
         console.log("Got response: " + res.statusCode);
         var bodyarr = [];

         res.on('data', function (chunk) {
         bodyarr.push(chunk);
         });
         res.on('end', function () {
         console.log(bodyarr.join('').toString());
         });
         }).on('error', function (e) {
         console.log("Got error: " + e.message);
         req.end();
         });
         fetchResult = function(url, callback) {
         var http = require('http');

         var bodyarr = [];
         http.get(url, function(res) {
         res.on('data', function(chunk){
         bodyarr.push(chunk);
         console.log(bodyarr);
         });
         res.on('end', function(){
         callback(bodyarr.join('').toString());
         });
         }).on('error', function(e) {
         callback(e.message);
         });
         }*/
//console.log('Send sms request\n', unicodeMessageText);
        /*var myMessage;
         try {
         myMessage = new websms.TextMessage(recipientAddressList,
         unicodeMessageText, creationFailedCallback);
         } catch (e) {
         console.log('Caught message creation error: ', e.message);
         console.log('Stacktrace: ', e.stack);
         return;
         }  myClient.send(myMessage, maxSmsPerMessage, isTest, transferredCallback,
         notTransferredCallback)
         }

         function transferredCallback(apiResponse, messageObject) {
         console.log("\n---- transferredCallback function called with ApiResponse:", apiResponse);
         console.log('\n---- Related messageObject:\n', messageObject);
         var statusCode      = apiResponse.statusCode;
         var statusMessage   = apiResponse.statusMessage;
         var transferId      = apiResponse.transferId;
         var clientMessageId = apiResponse.clientMessageId;
         }
         function notTransferredCallback(errorObj, messageObject){
         console.log("\n---- notTransferredCallback function called.\n");
         if (errorObj.cause === 'parameter' ||
         errorObj.cause === 'authorization' ||
         errorObj.cause === 'connection' ||
         errorObj.cause === 'unknown') {
         console.log(errorObj.message);
         }else if (errorObj.cause === 'api') {
         // API responded, but some limit was hit
         // statusCode and statusMessage are readable,
         // see API docs for codes
         var apiResponse = errorObj.apiResponse;
         var statusCode    = apiResponse.statusCode;
         var statusMessage = apiResponse.statusMessage;
         console.log('\n---- apiResponse:\n', apiResponse);
         }
         console.log('\n---- Related messageObject:\n', messageObject);
         }
         function creationFailedCallback(errorObj, incompleteMessageObject) {
         console.log("\n---- creationFailedCallback function called with errorObj:\n", errorObj);
         console.log("incompleteMessageObject:", incompleteMessageObject);
         }*/
