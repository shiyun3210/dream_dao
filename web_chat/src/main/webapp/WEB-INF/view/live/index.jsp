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
    <meta charset="utf-8">
    <title>MSE Live Demo</title>
    <link href="/resources/live/video-js.css" rel="stylesheet">
    <script src="/resources/live/videojs-ie8.min.js"></script>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://unpkg.com/awsm.css/dist/awsm.min.css">
    <link href="https://fonts.googleapis.com/css?family=PT+Sans|PT+Serif:400,400i,700,700i&amp;subset=cyrillic" rel="stylesheet">
</head>
<body>
<h1>MSE Live Demo</h1>
<video id="my-video" autoplay controls></video>
<script src="/resources/live/video.js"></script>
<script src="/resources/live/mux.js"></script>
<script src="/resources/live/flow-mux.js"></script>
<script src="/resources/live/flow.js"></script>
<script>
    // application.
    var mediaSource = new MediaSource();
    var video = document.getElementById("my-video");
    video.src = URL.createObjectURL(mediaSource);
    mediaSource.addEventListener('sourceopen', function (_) {
        var audioSourceBuffer = mediaSource.addSourceBuffer('video/mp4;codecs="mp4a.40.2"');
        var videoSourceBuffer = mediaSource.addSourceBuffer('video/mp4;codecs="avc1.42E01E"');
        var flow = new FlowTransmuxer();
        // append mp4 segment to mse.
        flow.on('data', function (segment) {
            if (segment.type == 'audio') {
                audioSourceBuffer.appendBuffer(segment.data.buffer);
            } else {
                videoSourceBuffer.appendBuffer(segment.data.buffer);
            }
        });
        var url = "ws://192.168.10.247:8327/media";
        console.log('start play flow ' + url);
        flow.src(url);
    });
</script>
</body>
</html>
