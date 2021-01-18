'use strict';

function isFullscreenEnabled() {
    return  document.fullscreenEnabled || 
            document.webkitFullscreenEnabled || 
            document.mozFullScreenEnabled || 
            document.msFullscreenEnabled;
}

function getFullscreenElement() {
    return  document.fullscreenElement || 
            document.webkitFullscreenElement || 
            document.mozFullScreenElement || 
            document.msFullscreenElement;
}

jQuery(document).ready(function($) {
    if (isFullscreenEnabled()) {
        function updateFullCheck() {
            if (getFullscreenElement()) {
                $('#fullCheck').prop('checked', true).checkboxradio('refresh');
            } else {
                $('#fullCheck').prop('checked', false).checkboxradio('refresh');
            }
        }

        var domElem = document.getElementById('fullDiv');

        domElem.addEventListener('fullscreenchange', updateFullCheck);
        domElem.addEventListener('webkitfullscreenchange', updateFullCheck);
        domElem.addEventListener('mozfullscreenchange', updateFullCheck);
        // for ie11 attach to the document instead of the DOM element
        document.addEventListener('MSFullscreenChange', updateFullCheck);

        $('#fullCheck').checkboxradio().click(function(ev) {
            ev.preventDefault();
            ev.stopPropagation();
            
            if (getFullscreenElement()) {
                if (document.exitFullscreen) {
                    document.exitFullscreen();
                } else if (document.mozCancelFullScreen) {
                    document.mozCancelFullScreen();
                } else if (document.webkitCancelFullScreen) {
                    document.webkitCancelFullScreen();
                } else if (document.msExitFullscreen) {
                    document.msExitFullscreen();
                    }
                } else {
                    if (domElem.requestFullscreen) {
                        domElem.requestFullscreen();
                    } else if (domElem.mozRequestFullScreen) {
                        domElem.mozRequestFullScreen();
                    } else if (domElem.webkitRequestFullscreen) {
                        domElem.webkitRequestFullscreen();
                    } else if (domElem.msRequestFullscreen) {
                        domElem.msRequestFullscreen();
                    }
                }
            }).checkboxradio('enable');
    }
});
