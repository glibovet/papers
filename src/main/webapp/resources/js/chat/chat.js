var stompClient = null;
var connectState = true;
var messagesLimit = 15;
var messagesOffset = 0;
var authUserId;
var eventId;
var chatId;
var isChatWithCreator;

$(document).ready(function () {
    authUserId = $("#authUserId").val();
    eventId = $("#event").val();
    chatId = $("#chat").val();
    isChatWithCreator = $("#chatWithCreator").val();
    loadPrevMessages();
});



function connect() {
    // var chat = document.getElementById('chat').value;
    var socket = new SockJS('/chat');

    stompClient = Stomp.over(socket);
    //var divElement = document.getElementById('sms');
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/messages/1', function (message) { //TODO +chatId
            showMessageOutput(JSON.parse(message.body), true);
        });
    });
    //divElement.scrollTop = 9999;
}

function disconnect() {
    setConnected(false);
    console.log("Disconnected");
}

function sendMessage() {
    // var event = document.getElementById('event').value;
    // var from = document.getElementById('from').value;
    // var text = document.getElementById('text').value;
    // var sender = document.getElementById('userId').value;
    // var chat = document.getElementById('chat').value;
    // var senderPhoto = document.getElementById('photo').value;

    stompClient.send("/app/chat/1", {},
        JSON.stringify({'message': "Hello message"}));
    // document.getElementById('text').value = "";
    // document.getElementById('sendMessage').disabled = true;
}

function showMessageOutput(message, isAppended) {
    alert("new mess "+message);
}

// function showMessageOutputFromData() {
//     console.log('FILTER MESSAGE');
//     var div = document.getElementById('showMessageOutput');
//     var divElement = document.getElementById('sms');
//     div.classList.add("text-right");
//     div.scrollTop = 9999;
// }

// function checkParams() {
//     var name = $('#text').val();
//
//     if (name.trim() != 0) {
//         $('#sendMessage').removeAttr('disabled');
//         if (event.keyCode === 13) {
//             $('#sendMessage').click();
//         }
//     } else {
//         $('#sendMessage').attr('disabled', 'disabled');
//     }
//
// }

// function loadPrevMessages() {
//     var sms = document.getElementById('sms');
//     if (sms.scrollTop === 0) {
//         $.ajax({
//             type: 'GET',
//             url: "/account/eventList/eventChat/main/getChatMessages",
//             dataType: 'json',
//             data:{
//                 eventId: eventId,
//                 chatId: chatId,
//                 state: isChatWithCreator,
//                 limit: messagesLimit,
//                 offset: messagesOffset
//             },
//             success: function (data) {
//                 console.log(JSON.stringify(data));
//                 data.forEach(function (message) {
//                     showMessageOutput(message, false); //TODO set last message id
//                 });
//                 if (messagesOffset === 0) {
//                     sms.scrollTop = 9999;
//                 } else {
//                     sms.scrollTop = 1;
//                 }
//                 messagesOffset += messagesLimit;
//             },
//             error: function (data) {
//                 console.log(JSON.stringify(data));
//             }
//         })
//     }
// }