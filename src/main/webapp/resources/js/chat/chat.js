var stompClient = null;
var userId = null;
var chatId = null;

$(document).ready(function () {
    userId = $("#userId").val();
    chatId = $("#chatId").val();
    connect();
    $('#text').keypress(function (e) {
        var key = e.which;
        if(key == 13) {
            $('#sendButton').click();
            return false;
        }
    });
    document.getElementById('chat').scrollTop = 9999;
});

function connect() {
    var socket = new SockJS('/papers');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/papers/' + chatId, function (message) {
            showMessage(JSON.parse(message.body));
        });
    });
}

function sendMessage() {
    var text = document.getElementById('text').value;
    if(text.trim() == 0){
        document.getElementById('text').value = "";
        return;
    }
    stompClient.send("/app/papers/" + chatId, {}, JSON.stringify({
        'userId': userId,
        'chatId': chatId,
        'text': text,
        'date': new Date(),
        'attachment': 'attachment'
    }));
    document.getElementById('text').value = "";
}

function showMessage(message) {
    console.log(message);
    var messageHtml= "";
    if(userId == message.userId){
        messageHtml = "<div class=\"mess odd\">";
    }else{
        messageHtml = "<div class=\"mess even\">";
    }
    messageHtml += "<div class=\"user_ph\">" +
        "<img class=\"user_ph\" src=\"/users/image/" + message.userId + "\"/>" +
        "</div>" +
        "<div class=\"mess\">" +
        "<div class=\"name\"><h3>" + message.userName +" " + message.userLastName + "</h3> <span> "+message.date+"</span></div>\n" +
        "<p>"+ message.text +"</p>" +
        "</div>" +
        "</div>";
    $("#chat_container").append($(messageHtml));
    document.getElementById('chat').scrollTop = 9999;
}

function disconnect() {
    stompClient.disconnect();
    console.log("Disconnected");
}