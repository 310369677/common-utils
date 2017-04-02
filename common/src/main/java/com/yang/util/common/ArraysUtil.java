package com.yang.util.common;

import java.util.*;

/**
 * 描述: 数组工具
 * 作者:杨川东
 * 时间:0:41
 */
public class ArraysUtil {

    public static String join(List list, String separator) {
        if (VerifyUtil.objIsEmpty(list)) {
            return "";
        }
        String defaultSeparator = ",";
        if (!VerifyUtil.objIsEmpty(separator)) {
            defaultSeparator = separator;
        }
        StringBuilder result = new StringBuilder("");
        for (Object object : list) {
            result.append(object.toString()).append(defaultSeparator);
        }
        return result.substring(0, result.length() - 1);
    }

    /**
     * 分割字符串数组[1,2,3] 将变成1,2,3
     * @param array 数组
     * @param separator 分割符
     * @return
     */
    public static String join(String[] array,String separator){
        return join(Arrays.asList(array),separator);
    }


    public static <T> List<T> mergeArray(T[] ... arrays) {
        List<T> list=new ArrayList<T>();
        for (T[] t:arrays){
            if(t==null){
                continue;
            }
            list.addAll(clearNullInArray(t));
        }
        return list;
    }


    /**
     * 清除数组中的null值
     * @param source 要清处的数组
     * @param <T> 数组的类型
     * @return 结果
     */
    public static <T> List<T> clearNullInArray(T[] source) {
        List<T> list = new ArrayList<T>();
        for (T t : source) {
            if (t == null) {
                continue;
            }
            list.add(t);
        }
        return list;
    }

    /**
     * 从数组中查找，找到就返回索引否则返回-1
     * @param arrays 需要查找的源数组
     * @param t 需要查找的值
     * @param comparator 比较器
     * @param <T> 类型
     * @return 查找到的索引
     */
    public static <T> int find(T[] arrays, T t, Comparator<T> comparator) {
        if (arrays == null) {
            return -1;
        }
        for (int i = 0; i < arrays.length; i++) {
            if(comparator!=null&&comparator.compare(arrays[i],t)==0){
                return i;
            }
            if (arrays[i] == t) {  //地址相等视为相等
                return i;
            }
            if (t instanceof Comparable) {
                Comparable comparable = (Comparable) t;
                int result = comparable.compareTo(arrays[i]);
                if (result == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static <T> int find(T[] array,T t){
        return find(array,t,null);
    }

    /**
     * 从一个集合中查找是否存在某个元素
     * @param collection 集合
     * @param t 需要查找的元素
     * @param comparator 比较器
     * @param <T> 类型
     * @return 查找到的元素
     */
    public static <T> T find(Collection<T> collection,T t,Comparator<T> comparator){
        for (T el:collection){
            if(comparator!=null&&(comparator.compare(el,t)==0)){
                return el;
            }
            if(el==t){
                return el;
            }
            if(t instanceof Comparable){
                Comparable comparable= (Comparable)t;
                if(comparable.compareTo(el)==0){
                    return el;
                }
            }
        }
        return null;
    }

    /**
     * 从一个集合中查找是否存在某个元素
     * @param collection 集合
     * @param t 需要查找的元素
     * @param <T> 类型
     * @return 集合中查找到的元素
     */
    public static <T> T find(Collection<T> collection,T t){
           return find(collection,t,null);
    }

}
