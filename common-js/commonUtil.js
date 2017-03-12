(function () {
    function Utils() {
        function objIsType(obj,type) {
            return Object.prototype.toString.call(obj)==="[object "+type+"]";
        }
        this.dynamicLoadScripts=function (urls, callback) {
            if (objIsType(urls,"String")) {  //单个脚本
                loadSingleScript(urls, callback);
            } else if (objIsType(urls,"Array")) {  //多个脚本
                var len = urls.length;
                var i = 0;
                fireNextScriptLoad(urls,i,len);
                function fireNextScriptLoad(urls,index,end) {
                    loadSingleScript(urls[index],function(){
                        if(index+1==end){
                            callback();
                            return;
                        }
                        fireNextScriptLoad(urls,index+1,end);
                    });
                }

            }
            //加载单个脚本
            function loadSingleScript(url, callback) {
                if (!objIsType(url,"String")) {
                    return;
                }
                if (url.endsWith(".js")) {
                    handleJs(url, callback);
                } else if (url.endsWith(".css")) {
                    handleCss(url, callback);
                }
            }

            //动态添加js
            function handleJs(url, callback) {
                var html = document.getElementsByTagName("html")[0];
                var script = document.createElement("script");
                script.src = url;
                script.type = "text/javascript";
                var done = false;
                script.onload = script.onreadystatechange = function () {
                    if (!done && (!this.readyState ||
                        this.readyState == 'loaded' || this.readyState == 'complete')) {
                        done = true;
                        if (typeof callback==="function")
                            callback();
                        // Handle memory leak in IE
                        script.onload = script.onreadystatechange = null;
                    }
                };
                html.appendChild(script);
            }

            //动态添加css
            function handleCss(url, callback) {
                var head = document.getElementsByTagName("head")[0];
                var link = document.createElement("link");
                link.href = url;
                link.rel = "stylesheet";
                var done = false;
                link.onload = link.onreadystatechange = function () {
                    if (!done && (!this.readyState ||
                        this.readyState == 'loaded' || this.readyState == 'complete')) {
                        done = true;
                        if (Utils.isFunction(callback))
                            callback();
                        // Handle memory leak in IE
                        link.onload = link.onreadystatechange = null;
                    }
                };
                head.appendChild(link);
            }
        }
    }
    window.Utils = new Utils();
})();
