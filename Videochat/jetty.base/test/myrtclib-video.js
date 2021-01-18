
    var RTCPeerConnection = null;
    var getUserMedia = null;
    var attachMediaStream = null;
    var reattachMediaStream = null;
    var webrtcDetectedBrowser = null;

    var room = null;
    var initiator;

    var localStream;
    var remoteStream;

    var pc = null;
    var signalingURL;

    var localVideo;
    var remoteVideo;

    var channelReady;
    var channel;

    var pc_config = {"iceServers":
       [{url:'stun:23.21.150.121'},
        {url:'stun:stun.l.google.com:19302'}]};

    var sdpConstraints = {'mandatory': {'OfferToReceiveAudio':true, 'OfferToReceiveVideo':true }};

    function myrtclibinit(sURL, lv, rv) {
        signalingURL = sURL;
        localVideo = lv;
        remoteVideo = rv;
        openChannel();
    };

    function openChannel() {
        channelReady = false;
        channel = new WebSocket(signalingURL);
        channel.onopen = onChannelOpened;
        channel.onmessage = onChannelMessage;
        channel.onclose = onChannelClosed;
    };

    function onChannelOpened() {
        channelReady = true;

        if(location.search.substring(1,5) == "room") {
            room = location.search.substring(6);
            sendMessage({"type" : "ENTERROOM", "value" : room * 1});
            initiator = true;
        } else {
            sendMessage({"type" : "GETROOM", "value" : ""});
            initiator = false;
        }
        doGetUserMedia();
    };

    function onChannelMessage(message) {
        processSignalingMessage(message.data);
    };

    function onChannelClosed() {
        channelReady = false;
    };

    function sendMessage(message) {
        var msgString = JSON.stringify(message);
        channel.send(msgString);
    };

    function processSignalingMessage(message) {
        var msg = JSON.parse(message);
        if (msg.type === 'offer') {
            pc.setRemoteDescription(new RTCSessionDescription(msg));
            doAnswer();
        } else if (msg.type === 'answer') {
            pc.setRemoteDescription(new RTCSessionDescription(msg));
        } else if (msg.type === 'candidate') {
            var candidate = new RTCIceCandidate({sdpMLineIndex:msg.label, candidate:msg.candidate});
            pc.addIceCandidate(candidate);
        } else if (msg.type === 'GETROOM') {
            room = msg.value;
//            OnRoomReceived(room);
        } else if (msg.type === 'WRONGROOM') {
            window.location.href = "/";
        }
    };

/* deprecated workaround:
navigator.getUserMedia = ( navigator.getUserMedia ||
                       navigator.webkitGetUserMedia ||
                       navigator.mozGetUserMedia ||
                       navigator.msGetUserMedia);
*/

    function doGetUserMedia() {
        var constraints = {"audio": true, "video": {"mandatory": {}, "optional": []}};
        try {
            navigator.mediaDevices.getUserMedia(constraints, onUserMediaSuccess,
                function(e) {
                        console.log("getUserMedia error "+ e.toString());
                });
        } catch (e) {
            console.log(e.toString());
        }
    };

    function onUserMediaSuccess(stream) {
        attachMediaStream(localVideo, stream);
        localStream = stream;
        createPeerConnection();
        pc.addStream(localStream);

        if (initiator) doCall();
    };

    function createPeerConnection() {
        var pc_constraints = {"optional": [{"DtlsSrtpKeyAgreement": true}]};
        try {
            pc = new RTCPeerConnection(pc_config, pc_constraints);
            pc.onicecandidate = onIceCandidate;
        } catch (e) {
            console.log(e.toString());
            pc = null;
            return;
        }
        pc.onaddstream = onRemoteStreamAdded;
    };

    function onIceCandidate(event) {
        if (event.candidate)
            sendMessage({type: 'candidate', label: event.candidate.sdpMLineIndex, id: event.candidate.sdpMid,
                candidate: event.candidate.candidate});
    };

    function onRemoteStreamAdded(event) {
        attachMediaStream(remoteVideo, event.stream);
        remoteStream = event.stream;
    };

    function doCall() {
        pc.createOffer(setLocalAndSendMessage, errorCallBack, sdpConstraints);
    };

    function doAnswer() {
        pc.createAnswer(setLocalAndSendMessage, errorCallBack, sdpConstraints);
    };

    function errorCallBack(e) {
        console.log("Something is wrong: " + e.toString());
    }

    function setLocalAndSendMessage(sessionDescription) {
        pc.setLocalDescription(sessionDescription);
        sendMessage(sessionDescription);
    };




