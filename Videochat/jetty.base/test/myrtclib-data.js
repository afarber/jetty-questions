
    var RTCPeerConnection = null;

    var room = null;
    var initiator;

    var pc = null;
    var signalingURL;

    var data_channel = null;

    var channelReady;
    var channel;

    var pc_config = {"iceServers": [
        {url:'stun:23.21.150.121'},
        {url:'stun:stun.l.google.com:19302'}
    ]};

    function myrtclibinit(sURL) {
        signalingURL = sURL;
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
        createPeerConnection();

        if(location.search.substring(1,5) == "room") {
            room = location.search.substring(6);
            sendMessage({"type" : "ENTERROOM", "value" : room * 1});
            initiator = true;
            doCall();
        } else {
            sendMessage({"type" : "GETROOM", "value" : ""});
            initiator = false;
        }
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
            onRoomReceived(room);
        } else if (msg.type === 'WRONGROOM') {
            window.location.href = "/";
        }
    };

    function createPeerConnection() {
        try {
            pc = new RTCPeerConnection(pc_config, null);
            pc.onicecandidate = onIceCandidate;
            pc.ondatachannel = onDataChannel;
        } catch (e) {
            console.log(e);
            pc = null;
            return;
        }
    };

    function onDataChannel(evt) {
        console.log('Received data channel creating request');
        data_channel = evt.channel;
        initDataChannel();
    }

    function createDataChannel(role) {
        try {
            data_channel = pc.createDataChannel("datachannel_"+room+role, null);
        } catch (e) {
            console.log('error creating data channel ' + e);
            return;
        }
        initDataChannel();
    }

    function initDataChannel() {
        data_channel.onopen = onChannelStateChange;
        data_channel.onclose = onChannelStateChange;
        data_channel.onmessage = onReceiveMessageCallback;
    }

    function onIceCandidate(event) {
        if (event.candidate) {
            sendMessage({
                type: 'candidate', 
                label: event.candidate.sdpMLineIndex, 
                id: event.candidate.sdpMid,
                candidate: event.candidate.candidate
            });
        }
    };

    function failureCallback(e) {
        console.log("failure callback "+ e.message);
    }

    function doCall() {
        createDataChannel("caller");
        pc.createOffer(setLocalAndSendMessage, failureCallback, null);
    };

    function doAnswer() {
        pc.createAnswer(setLocalAndSendMessage, failureCallback, null);
    };

    function setLocalAndSendMessage(sessionDescription) {
        pc.setLocalDescription(sessionDescription);
        sendMessage(sessionDescription);
    };

    function sendDataMessage(data) {
        data_channel.send(data);
    };

    function onChannelStateChange() {
        console.log('Data channel state is: ' + data_channel.readyState);
    }

    function onReceiveMessageCallback(event) {
        console.log(event);
        try {
            var msg = JSON.parse(event.data);
            if (msg.type === 'chatmessage') {
                onPrivateMessageReceived(msg.txt);
            }
        }
        catch (e) {}
    };
