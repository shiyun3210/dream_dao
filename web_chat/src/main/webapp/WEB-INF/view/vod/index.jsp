<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getServerName() + ":"
			+ request.getServerPort() + path + "/";
	String basePath2 = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN"
"http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <meta charset="utf-8"/>
    <title>MSE VOD Demo</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://unpkg.com/awsm.css/dist/awsm.min.css">
    <link href="https://fonts.googleapis.com/css?family=PT+Sans|PT+Serif:400,400i,700,700i&amp;subset=cyrillic" rel="stylesheet">
</head>
<body>
<h1>MSE VOD Demo</h1>
<video autoplay controls></video>
<script>
    var video = document.querySelector('video');
    var wsURL = 'ws://192.168.10.247:83/ws?uid=1';
    // Need to be specific for Blink regarding codecs
    // ./mp4info test.mp4 | grep Codec
    var mimeCodec = 'video/mp4; codecs="mp4a.40.2, avc1.42e01e"';
    if ('MediaSource' in window && MediaSource.isTypeSupported(mimeCodec)) {
        var mediaSource = new MediaSource;
        console.log('media state:'+mediaSource.readyState); // closed
        video.src = URL.createObjectURL(mediaSource);
        mediaSource.addEventListener('sourceopen', sourceOpen);
    } else {
        console.error('Unsupported MIME type or codec: ', mimeCodec);
    }
    function sourceOpen(_) {
        console.log('media state:'+this.readyState); // open
        var mediaSource = this;
        var sourceBuffer = mediaSource.addSourceBuffer(mimeCodec);
        fetchMedia(wsURL, function (buf) {
            sourceBuffer.addEventListener('updateend', function (_) {
                mediaSource.endOfStream();
                video.play();
                console.log('media state:'+mediaSource.readyState); // ended
                
            });
            sourceBuffer.appendBuffer(buf);
        });
    };
    function fetchMedia(url, cb) {
        console.log(url);
        ws = new WebSocket(url);
        ws.binaryType = "arraybuffer";
        ws.onopen = function () {
            console.log("ws connected")
        };
        ws.onmessage = function (event) {
        	console.info(event.data);
            cb(event.data);
        };
        ws.onclose = function () {
            console.log("ws closed")
        };
        ws.onerror = function (e) {
            console.log(e.msg);
        };
    };
</script>
</body>
</html>
