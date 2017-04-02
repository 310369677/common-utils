/**
 * Created by 杨川东 on 2017/3/27.
 *
 */
(function () {
    String.prototype.endWith=function (str) {
        var reg=new RegExp(quoteReplacement(str)+"$");
        return reg.test(this);
    };

    String.prototype.startWith=function (str) {

        var reg=new RegExp("^"+quoteReplacement(str));
        return reg.test(this);
    };

    /**
     * 用新的str替换所有老的str
     * @param oldStr 老的str
     * @param newStr 新的str
     */
    String.prototype.replaceAll=function (oldStr,newStr) {
        var str=quoteReplacement(oldStr);
        return this.replace(new RegExp(str,"gm"),newStr);
    };

    function quoteReplacement(str) {
        if((str.indexOf('\\')==-1)&&(str.indexOf('$')==-1)){
            return str;
        }
        var result="";
        for (var i=0;i<str.length;i++){
            var c=str.charAt(i);
            if(c==='\\'||c==='$'){
                result+='\\';
            }
            result+=c;
        }
        return result;
    }
})();



