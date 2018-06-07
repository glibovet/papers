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
            send();
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

function send() {
    if(getFileName()) {
        sendMessageWithFile();
    }else{
        sendMessage();
    }
}

function sendMessageWithFile() {
    var csrf = document.getElementById('_csrf').value;
    $.ajax({
        url: "/api/storage/attachment?_csrf=" + csrf,
        type: "POST",
        data: new FormData($("#upload-file-form")[0]),
        enctype: 'multipart/form-data',
        processData: false,
        contentType: false,
        cache: false,
        success: function () {
            sendMessage();
        },
        error: function () {
            // Handle upload error
            alert("error");
        }
    });
}

function sendMessage(){
    var text = document.getElementById('text').value;
    console.log("text "+text);
    var fileName = getFileName();
    if(text.trim() == 0){
        document.getElementById('text').value = "";
        return;
    }
    stompClient.send("/app/papers/" + chatId, {}, JSON.stringify({
        'messageId': -1,
        'userId': userId,
        'chatId': chatId,
        'text': text,
        'date': new Date(),
        'attachment': fileName
    }));
    var input = $("#upload-file-input");
    input.replaceWith(input.val('').clone(true));
    document.getElementById('text').value = "";
}

function getFileName(){
    var files = document.getElementById("upload-file-input").files;
    if(files.length === 0) return "";
    return files[0].name;
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
        "<p>"+ message.text +"</p>" ;
    if(message.attachment){
        messageHtml += "<a class=\"att\" href=\"/chat/message-attachment/"+message.messageId+"\">"+message.attachment+"</a>"
    }
    messageHtml += "</div>" +
        "</div>";
    $("#chat_container").append($(messageHtml));
    document.getElementById('chat').scrollTop = 9999;
}

function disconnect() {
    stompClient.disconnect();
    console.log("Disconnected");
}