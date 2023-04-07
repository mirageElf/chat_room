let coco = (function ($) {
    $.config = {
        base_server: '47.98.48.155', // 服务器ip
        // base_server: 'localhost', // 本地ip
        ws_url: 'ws://47.98.48.155/websocket/', // websocket地址
        // ws_url: 'ws://localhost/websocket/', // websocket地址
    };
    return $;
})(window.config||{});