package com.yang.util.file;

/**
 * 描述:
 * 作者:杨川东
 * 时间:22:13
 */
public class FileUtil {
    /**
     * 路径的拼接
     * @param paths 路劲
     * @return 拼接好的路劲
     */
    public static String joinPath(String ...paths){
        StringBuilder result=new StringBuilder("");
        for (String path:paths){
            if(path==null){
                continue;
            }
            path=path.replace("\\","/");
            if(path.startsWith("/")){
                path=path.substring(1);
            }
            if(path.endsWith("/")){
                path=path.substring(0,path.length()-1);
            }
            result.append(path).append("/");
        }
        return result.substring(0,result.length()-1);  //去掉最后的斜杠

    }
}
