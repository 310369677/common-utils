package com.yang.util.common;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * 描述:
 * 创建人: 杨川东
 * 时间:   2016-10-7
 */
public class VerifyUtil {
    /**
     * 校验一个对象是否为空
     * @param obj 被校验的对象
     * @return 返回校验的结果
     */
    public static boolean objIsEmpty(Object obj){
          if(obj==null)
            return true;
          if(obj instanceof String){
              return ((String) obj).trim().length()==0;
          }else if(obj instanceof Collection){
              return ((Collection) obj).size()==0;
          }else if(obj instanceof Map){
              return ((Map) obj).size()==0;
          }else if(obj.getClass().isArray()){
              return Array.getLength(obj)==0;
          }
          return false;
    }
}
