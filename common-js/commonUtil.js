(function () {
    function Utils() {
        //判断对象是不是某个类型
        this.objIsSpecifyType=function objIsSpecifyType(obj,type) {
            return Object.prototype.toString.call(obj)==="[object "+type+"]";
        };
        //判断是不是函数
        this.isFunction=function (fun) {
            return typeof fun == "function";
        };
        //判断是不是字符串
        this.isString=function (str) {
            return this.objIsSpecifyType(str,"String");
        };

        //判断是不是数组
        this.isArray=function (array) {
            return this.objIsSpecifyType(array,"Array");
        };

        //判断是不是NULL
        this.isNULL=function (obj) {
            return this.objIsSpecifyType(obj,"Null")
        };

        //判断是不是数字
        this.isNumber=function (obj) {
            obj=Number(obj);
            return !(obj.toString()==="NaN");
        };

        //判断是不是空
        this.isEmpty=function (obj) {
            if(typeof obj==="undefined"||this.isNULL(obj)||obj=="null"){
                return true;
            }
            if(this.isString(obj)||this.isArray(obj)){
                return obj.length===0;
            }
            return false;
        };
        /**
         * 动态加载脚本
         * @param urls 可以是单个脚本，也可以是脚本数组
         * @param callback 脚本加载完成的回掉函数
         */
        this.dynamicLoadScripts=function (urls, callback) {
            var util=this;
            if (this.isString(urls)) {  //单个脚本
                loadSingleScript(urls, callback);
            } else if (this.isArray(urls)) {  //多个脚本
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
                if (!util.isString(url)) {
                    return;
                }
                if (url.endWith(".js")) {
                    handleJs(url, callback);
                } else if (url.endWith(".css")) {
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
        };
        /**
         * 生成uuid
         * @param digit uuid的位数 可以是36也可以是32
         * @returns {string} 生成uuid的值
         */
        this.generateUUID=function (digit) {
            var defaultDigit=32;
            if(this.isNumber(digit)&&parseInt(digit)==36){
                defaultDigit=digit;
            }
            var str="xxxxxxxxxxxx4xxxyxxxxxxxxxxxxxxx";
            if(defaultDigit===36){
                str="xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx";
            }
            var d = new Date().getTime();
            return str.replace(/[xy]/g, function (c) {
                var r = (d + Math.random() * 16) % 16 | 0;
                d = Math.floor(d / 16);
                return (c == 'x' ? r : (r & 0x3 | 0x8)).toString(16);
            });
        };
    }
    window.Utils = new Utils();
})();
