var PHOTO_HAPPY = '/videochat/images/female_happy.png';
var PHOTO_SAD = '/videochat/images/female_sad.png';
var PHOTO_ERROR_HAPPY = '/videochat/images/male_happy.png';
var PHOTO_ERROR_SAD = '/videochat/images/male_sad.png';
var DETAILS_OPEN = '/videochat/images/details_open.png';
var DETAILS_CLOSE = '/videochat/images/details_close.png';
var LOADER_GIF = '/videochat/images/loader.gif';

function brokenImage(img) {
    img.onerror = "";
    img.src = PHOTO_ERROR_HAPPY;
    return true;
}

function escapeHtml(text) {
    var characters = {
        '&': '&amp;',
        '"': '&quot;',
        "'": '&#039;',
        '<': '&lt;',
        '>': '&gt;'
    };

    return (text + "").replace(/[<>&"']/g, function(x) {
        return characters[x];
    });
}

function chessPiece(elo) {
    if (elo > 2600) {
        return '&#9812;';
    }

    if (elo > 2400) {
        return '&#9813;';
    }

    if (elo > 2200) {
        return '&#9814;';
    }

    if (elo > 2000) {
        return '&#9816;';
    }

    if (elo > 1800) {
        return '&#9815;';
    }

    return '&#9817;';
}

function hexFromRGB(r, g, b) {
    var hex = [
        r.toString(16),
        g.toString(16),
        b.toString(16)
    ];
    $.each(hex, function(nr, val) {
        if (val.length === 1) {
            hex[nr] = '0' + val;
        }
    });
    return hex.join('').toUpperCase();
}

function isIframe () {
    try {
        return window.self !== window.top;
    } catch (ex) {
        return true;
    }
}
