(function () {
    function VerifyUtil() {
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
        }
    }
    window.VerifyUtil=new VerifyUtil();
}());
