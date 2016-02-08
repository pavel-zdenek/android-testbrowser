/*
LANDMINE WARNING: Android Webview up to version 44 in API 23 blows on single-line // comments
I/chromium: [INFO:CONSOLE(1)] "Uncaught SyntaxError: Unexpected end of input", source:  (1)
https://code.google.com/p/android/issues/detail?id=23437
*/

(function(scope) {
  scope.__NATIVE_INTERFACE_NAME__.threadDeterminationCallback();
  scope.shouldBlock = function(urlString) {
    try {
      var url = new URL(urlString);
    } catch(e) {
      return false;
    }
    var pathname = url.pathname;
    if (!pathname || pathname.length === 0) {
      return false;
    }
    var suffixes = ['.jpg', '.jpeg', '.png', '.gif'];
    for(var idx in suffixes) {
      var suffix = suffixes[idx];
      /* Android 21 WebView does not implement String.prototype.endsWith */
      if (pathname.substr(-suffix.length) == suffix) {
        return true;
      }
    }
    return false;
  };
  return 'OK';
})(window);
