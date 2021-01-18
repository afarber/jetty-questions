'use strict';

var mode4 = false,
    player0,
    player1,
    player2,
    player3;

var WIDTH  = 1200,
    HEIGHT = 900,
    RATIO  = WIDTH / HEIGHT,
    app;

function resizeApp(w, h) {
    w = Math.floor(0.98 * w);
    h = Math.floor(0.98 * h);

    //console.log('resizeApp w x h: ' + w + ' x ' + h);

    $('#fullDiv').width(w);
    $('#fullDiv').height(h);

    if (mode4) {
        player0.css({
            position: 'absolute',
            left: (w - player0.width()) / 2 + 'px',
            bottom: '4px'
        });

        player1.css({
            position: 'absolute',
            left: '4px',
            top: (h - player1.height()) / 2 + 'px'
        });

        player2.css({
            position: 'absolute',
            left: (w - player0.width()) / 2 + 'px',
            top: '4px'
        });

        player3.css({
            position: 'absolute',
            left: (w - player2.width() - 4) + 'px',
            top: (h - player2.height()) / 2 + 'px'
        }).show();
    } else {
        player0.css({
            position: 'absolute',
            left: (w - player0.width()) / 2 + 'px',
            bottom: '4px'
        });

        player1.css({
            position: 'absolute',
            left: '4px',
            top: '4px'
        });

        player2.css({
            position: 'absolute',
            left: (w - player2.width() - 4) + 'px',
            top: '4px'
        });

        player3.hide();
    }
    
    if (app) {
        var canvasRatio = $('#pixiCanvas').width() / $('#pixiCanvas').height();
        //console.log('resizeApp pixiCanvas: ' + $('#pixiCanvas').width() + ' x ' + $('#pixiCanvas').height());
        
        var scaleX = 1, scaleY = 1, offsetX = 0, offsetY = 0;

        // if canvas is too wide
        if (canvasRatio > RATIO) {
            scaleX = RATIO / canvasRatio;
            scaleY = 1;
            offsetX = (1 - scaleX) * WIDTH / 2;
            offsetY = 0;
        } else {
            scaleX = 1;
            scaleY = canvasRatio / RATIO;
            offsetX = 0;
            offsetY = (1 - scaleY) * HEIGHT / 2;
        }

        app.stage.scale.set(scaleX, scaleY);
        app.stage.position.set(offsetX, offsetY);
    }
}

jQuery(document).ready(function($) {
    var ws,
        shadow = new Shadow(),
        charm = new Charm(PIXI),
        myCards = [],
        leftCards = [],
        rightCards = [],
        tableCards = [],
        oldX = 0,
        oldY = 0,
        PHOTO_PATTERN = /^https:/i,
        CENTER = {
            my: 'center',
            at: 'center',
            of: $('#pixiCanvas')
        };

    app = new PIXI.Application({
        width: WIDTH,
        height: HEIGHT,
        view: document.getElementById('pixiCanvas'),
        antialias: true,
        transparent: true
    });

    app.ticker.add(function(time){
        charm.update();
    });
    
    var background = new PIXI.Graphics();
    for (var i = 0; i < 8; i++) {
        for (var j = 0; j < 6; j++) {
            if ((i + j) % 2 == 0) {
                background.beginFill(0xCCCCFF);
                background.drawRect(i * WIDTH / 8, j * WIDTH / 8, WIDTH / 8, WIDTH / 8);
                background.endFill();
            }
        }
    }
    app.stage.addChild(background);

    var tablesDiv = $('#tablesDiv');
    var playersDiv = $('#playersDiv');

    var table1 = $('<div/>').appendTo(tablesDiv).table({ tid: 3 });
    var table2 = $('<div/>').appendTo(tablesDiv).table({
        tid: 104,
        player0: {
            rep: Math.random(),
            photo: 'http://afarber.de/images/farber.jpg'
        }
    });
    var table3 = $('<div/>').appendTo(tablesDiv).table({
        tid: 115,
        player0: {
            rep: Math.random()
        },
        player1: {
        },
        player2: {
            rep: Math.random(),
            photo: '/raspasy/images/male_sad.png'
        },
        player3: {
            rep: Math.random(),
            photo: '/raspasy/images/female_happy.png'
        }
    });
    var table4 = $('<div/>').appendTo(tablesDiv).table({
        tid: 116,
        player0: {
            photo: '/raspasy/images/female_happy.png'
        },
        player1: {
            rep: Math.random(),
            photo: '/raspasy/images/female_sad.png'
        },
        player2: {
            rep: Math.random(),
            photo: '/raspasy/images/male_sad.png'
        },
        player3: {
            rep: Math.random(),
            photo: '/raspasy/images/female_happy.png'
        }
    });
    
    tablesDiv.on('click', '.raspasy-table', function() {
        var tid = $(this).table('option', 'tid');
        var obj = {
            social: SOCIAL,
            sid:    SID,
            auth:   AUTH
        };

        if (tid > 4) {
            obj.action = 'join';
            obj.tid = tid;
        } else if (tid == 4) {
            obj.action = 'new4';
        } else {
            obj.action = 'new3';
        }

        ws.send(JSON.stringify(obj));
    });

    function refreshSwatch() {
        var red   = $('#red').slider('value'),
            green = $('#green').slider('value'),
            blue  = $('#blue').slider('value'),
            hex   = '#' + hexFromRGB(red, green, blue);
        $('#swatch').text(hex);
        $('#swatch').css('background-color', hex);
        $('#pixiCanvas').css('background', 'radial-gradient(#FFFFFF, ' + hex + ')');
    }

    var kukuSlider = $('#kukuSlider').slider({
        orientation: 'horizontal',
        range: 'min',
        min: 1,
        max: 10,
        value: 10,
        slide: function(event, ui) {
            kukuSound.volume   = ui.value / 10;
            beepSound.volume   = ui.value / 10;
            perronSound.volume = ui.value / 10;
            postojSound.volume = ui.value / 10;
            trainSound.volume  = ui.value / 10;
            tundraSound.volume = ui.value / 10;
            waggonSound.volume = ui.value / 10;
        }
    });

    $('#red, #green, #blue').slider({
        orientation: 'horizontal',
        range: 'min',
        slide: refreshSwatch,
        change: refreshSwatch,
        max: 255,
        value: 127
    });
    $('#red').slider('value', 0x99);
    $('#green').slider('value', 0xCC);
    $('#blue').slider('value', 0x99);

    player0 = $('#player0').player({ 
        first: 'Me',
        photo: 'http://afarber.de/images/farber.jpg',
        pos: 1,
        bid: 'Пас'
    }).css({
        position: 'absolute'
    });

    player1 = $('#player1').player({ 
        first: 'Ms. Left',
        photo: 'https://raspasy.de/raspasy/images/female_happy.png',
        pos: 2,
        bid: 'Пас'
    }).css({
        position: 'absolute'
    });

    player2 = $('#player2').player({ 
        first: 'Mr. Right',
        photo: 'https://raspasy.de/raspasy/images/male_happy.png',
        pos: 3,
        bid: 'Пас'
    }).css({
        position: 'absolute'
    });

    player3 = $('#player3').player({ 
        first: 'Mr. Vier',
        photo: 'https://raspasy.de/raspasy/images/male_sad.png',
        pos: 4,
        bid: 'Пас'
    }).css({
        position: 'absolute'
    });

    var count = 0;

    function wobbleCard() {
        count += 0.1;
        //card.scale.x = 0.6 + Math.sin(count) * 0.02;
        //card.scale.y = 0.6 + Math.cos(count) * 0.02;
    }

    function setup(loader, resources) {
        var myStr = ['7♠', '8♠', '9♠', '10♠', 'J♠', 'Q♠', 'K♠', 'A♠'];
        for (var i = 0; i < myStr.length; i++) {
            var card = new Card(myStr[i], onDragStart, onDragMove, onDragEnd);
            card.rotation = (i - 3) * 0.1;
            card.move(app.renderer.width / 2 + (i - 3) * card.width / 4, 0.9 * app.renderer.height);
            app.stage.addChild(card);
            myCards.push(card);
        }
    }

    function connect() {
        ws = new WebSocket(WS_URL);

        ws.onopen = function(ev) {
            ws.send(JSON.stringify(LOGIN));
        };

        ws.onmessage = function(msg) {
            try {
                var obj = JSON.parse(msg.data);
                console.log(obj);
            } catch (ex) {
                console.log(ex);
                ws.close();
                return;
            }

            if ($.isPlainObject(obj)) { 
                switch(obj.action) {
                    case "setLobby":
                        setLobby(obj);
                        return;
                    case "addLobby":
                        addLobby(obj);
                        return;
                    case "remLobby":
                        remLobby(obj);
                        return;
                    case "setTable":
                        setTable(obj);
                        return;
                }
            }

            // TODO only call once
            kukuBtn.button('enable');
            chatInput[0].disabled = false;
        };

        ws.onerror = function(ev) {
            console.log('onerror: ' + ev.code + ' ' + ev.reason);
            disconnect();
        };

        ws.onclose = function(ev) {
            console.log('onclose: ' + ev.code + ' ' + ev.reason);
            disconnect();
        };
    }

    function disconnect() {
        kukuBtn.button('disable');
        chatInput[0].disabled = true;
        chatInput.val('');
    }

    var beepSound   = document.createElement('audio');
    beepSound.src   = '/raspasy/sounds/beep.mp3';
    var kukuSound   = document.createElement('audio');
    kukuSound.src   = '/raspasy/sounds/kuku.mp3';
    var perronSound = document.createElement('audio');
    perronSound.src = '/raspasy/sounds/perron.mp3';
    var postojSound = document.createElement('audio');
    postojSound.src = '/raspasy/sounds/postoj.mp3';
    var trainSound  = document.createElement('audio');
    trainSound.src  = '/raspasy/sounds/train.mp3';
    var tundraSound = document.createElement('audio');
    tundraSound.src = '/raspasy/sounds/tundra.mp3';
    var waggonSound = document.createElement('audio');
    waggonSound.src = '/raspasy/sounds/waggon.mp3';

    app.loader
        // TODO use smaller cards 172x200 and scale them up
        .add('/raspasy/images/cards254x400.json')
        //.add('/raspasy/images/cards508x800.json')
        .load(setup);

    function onDragStart(ev) {
        this.data = ev.data;
        var pos = this.data.getLocalPosition(this.parent);
        this.x = pos.x;
        this.y = pos.y;
        this.scale.x = Card.BIG_SCALE;
        this.scale.y = Card.BIG_SCALE;
        shadow.x = this.x + Shadow.OFFSET;
        shadow.y = this.y + Shadow.OFFSET;
        shadow.rotation = this.rotation = this.rotation + Math.random() - 0.5;
        app.stage.removeChild(this);
        app.stage.addChild(shadow);
        app.stage.addChild(this);
    }

    function onDragMove() {
        if (this.data) {
            var pos = this.data.getLocalPosition(this.parent);
            this.x = pos.x;
            this.y = pos.y;
            shadow.x = this.x + Shadow.OFFSET;
            shadow.y = this.y + Shadow.OFFSET;
        }
    }

    function onDragEnd() {
        if (this.data) {
            this.data = null;
            this.scale.x = Card.SMALL_SCALE;
            this.scale.y = Card.SMALL_SCALE;
            app.stage.removeChild(shadow);
        }
    }

    var settingsDlg = $('#settingsDlg').dialog({
        minWidth: 500,
        modal: true,
        appendTo: '#fullDiv',
        autoOpen: false,
        //position: CENTER,
        buttons: {
            'Закрыть': function() {
                // TODO save in cookies
                $(this).dialog('close');
            }
        }
    });

    var settingsBtn = $('#settingsBtn').button().click(function(ev) {
        ev.preventDefault();

        settingsDlg.dialog('open');
    });

    var kukuBtn = $('#kukuBtn').button().click(function(ev) {
        ev.preventDefault();

        kukuSound.play();
    });

    var lobbyBtn = $('#lobbyBtn').button().click(function(ev) {
        ev.preventDefault();

        var obj = {
            social: SOCIAL,
            sid:    SID,
            auth:   AUTH,
            action: 'leave'
        };
        ws.send(JSON.stringify(obj));

        //showLobby();
    });

    var scoreBtn = $('#scoreBtn').button().click(function(ev) {
        ev.preventDefault();

        // TODO
    });

    var chatInput = $('#chatInput').on('keyup input', function(ev) {
        if (ev.keyCode == 13) {
            sendChat();
        }
    });

    function sendChat() {
        var msg = chatInput.val().trim();
        if (msg.length == 0 || msg.length > 250) {
            return;
        }
/*
        $('#chatDiv').html('');
        var chat = {
            social: SOCIAL,
            sid:    SID,
            auth:   AUTH,
            action: 'chat',
            gid:    gid,
            msg:    msg
        };
        ws.send(JSON.stringify(chat));
*/
        chatInput.val('');
        chatInput.focus();
    }

    $('#invisCheck').change(function(ev) {
        if (this.checked) {
            // TODO display white rect on top
            kukuSlider.slider('value', 1);
            kukuSound.volume   = 0.05;
            beepSound.volume   = 0.05;
            perronSound.volume = 0.05;
            postojSound.volume = 0.05;
            trainSound.volume  = 0.05;
            tundraSound.volume = 0.05;
            waggonSound.volume = 0.05;
        } else {
            // TODO hide white rect on top
            kukuSlider.slider('value', 8);
            kukuSound.volume   = 0.8;
            beepSound.volume   = 0.8;
            perronSound.volume = 0.8;
            postojSound.volume = 0.8;
            trainSound.volume  = 0.8;
            tundraSound.volume = 0.8;
            waggonSound.volume = 0.8;
        }
    });
    
    function showGame(tid) {
        lobbyBtn.button('enable');
        settingsBtn.button('enable');
        $('#gameDiv').show();
        $('#lobbyDiv').hide();
        $('#chatDiv').hide();
        window.onresize();
    }
    
    function showLobby() {
        lobbyBtn.button('disable');
        settingsBtn.button('disable');
        $('#lobbyDiv').show();
        $('#chatDiv').show();
        $('#gameDiv').hide();
        window.onresize();
    }

    function setLobby(obj) {
        playersDiv.empty();
        var players = obj.players;
        for (var i = 0; i < players.length; i++) {
            var playerObj = players[i];

            $('<div/>', {id: 'lobbyPlayer' + playerObj.pid}).player({
                rep: playerObj.rep,
                given: playerObj.given,
                photo: playerObj.photo
            }).appendTo(playersDiv);

            $('<br/>', {id: 'lobbyPlayerBr' + playerObj.pid}).appendTo(playersDiv);
        }
        
        $('#lobbyCount').text($('#playersDiv br').length);
        showLobby();
    }

    function addLobby(playerObj) {
        var divId = 'lobbyPlayer' + playerObj.pid;
        var brId = 'lobbyPlayerBr' + playerObj.pid;

        if (document.getElementById(divId)) {
            // the div already exists, do not add twice
            return;
        }

        $('<div/>', {id: divId}).player({
            rep: playerObj.rep,
            given: playerObj.given,
            photo: playerObj.photo
        }).appendTo(playersDiv);

        $('<br/>', {id: brId}).appendTo(playersDiv);

        // just count <br> children of playersDiv
        $('#lobbyCount').text($('#playersDiv br').length);
    }

    function remLobby(playerObj) {
        $('#lobbyPlayer' + playerObj.pid).remove();
        $('#lobbyPlayerBr' + playerObj.pid).remove();
        // just count <br> children of playersDiv
        $('#lobbyCount').text($('#playersDiv br').length);
    }

    function setTable(tableObj) {
        showGame(tableObj.tid);
    }

    window.onresize = function() {
        if (!getFullscreenElement() && isIframe()) { 
            try {
                // request dimensions from parent site and call resizeApp() in the callback
                requestIframeDimensions();
                return;
            } catch (ex) {
                // can fail if parent site SDK not yet initialized
            }
        }

        var w = Math.min(window.innerWidth, screen.width);
        var h = Math.min(window.innerHeight, screen.height);
        resizeApp(w, h);
    };

    // will call window.onresize();
    showLobby();
    connect();
});

