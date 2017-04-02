/**
 * Created by 杨川东 on 2017/3/27.
 *
 */
(function () {
    function Browser() {
        var userAgent = navigator.userAgent;
        this.isOpera = function () {
            return userAgent.indexOf("Opera") > -1;
        };
        this.isIE = function () {
            return (userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !this.isOpera());
        };
        this.isFireFox=function () {
            return userAgent.indexOf("Firefox") > -1;
        };

        this.isChrome=function () {
            return userAgent.indexOf("Chrome") > -1;
        };

        this.isSafari=function () {
            return userAgent.indexOf("Safari") > -1;
        };

        this.getBrowserName=function () {
          if(this.isIE()){
              return "IE";
          }
          if(this.isOpera()){
              return "Opera";
          }
          if(this.isChrome()){
              return "Chrome";
          }
          if(this.isFireFox()){
              return "Firefox";
          }

          if(this.isSafari()){
              return "Safari";
          }
        }
    }
    window.Browser=new Browser();
})();