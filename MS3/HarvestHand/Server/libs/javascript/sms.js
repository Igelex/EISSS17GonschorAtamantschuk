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

function main(){
var myClient = new websms.Client(gatewayUrl, username, password);
var requestUrl = 'https://franziska.gonschor@web.de:theisss17@api.websms.com/rest/smsmessaging/simple?recipientAddressList=4917696217295&messageContent=hello%20world!&test=true';
console.log('Send sms request\n', unicodeMessageText);
var myMessage;
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
}
main();
