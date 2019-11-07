package com.yuezhu.util;

import org.springframework.beans.BeanUtils;
import us.codecraft.webmagic.ResultItems;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * @program: crawler
 * @description: 反射工具...
 * @author: Mr.Chen
 * @create: 2019-08-30 20:46
 **/
public class ReflectionUtil {
    /**
    * @Description: 属性copey....1
            * @Param:
            * @Return:
            * @Author: Mr.Chen
            * @Date: 2019/8/30 20:56
            */
public static void copeFields(Map<String,Object> source,Object target){
    if(source.size()>0){
        for(Map.Entry<String, Object> entry: source.entrySet()){
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            setFieldValue(target, fieldName, value);
        }
    }
}
/**
* @Description: 属性copy2....
        * @Param:
        * @Return:
        * @Author: Mr.Chen
        * @Date: 2019/8/30 20:57
        */

    public static void setProperty(Object ob, Class cls, ResultItems resultItems){
        PropertyDescriptor[] proDescrtptors = BeanUtils.getPropertyDescriptors(cls);
        if (proDescrtptors != null && proDescrtptors.length > 0) {
            for (PropertyDescriptor propDesc : proDescrtptors) {
                Method methodSet = propDesc.getWriteMethod();
                if(null != methodSet){
                    try {
                            methodSet.invoke(ob, (Object)resultItems.get(propDesc.getName()));
                    } catch (Exception e) {
                        if(e instanceof  IllegalArgumentException){
                            continue;
                        }
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    /**
     * 直接设置对象属性值, 忽略 private/protected 修饰符, 也不经过 setter
     * @param object
     * @param fieldName
     * @param value
     */
    public static void setFieldValue(Object object, String fieldName, Object value){
        Field field = getDeclaredField(object, fieldName);
        if (field == null)
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
        makeAccessible(field);
        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {}
    }




    /**
     * 循环向上转型, 获取对象的 DeclaredField
     * @param object
     * @param filedName
     * @return
     */
    public static Field getDeclaredField(Object object, String filedName){

        for(Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()){
            try {
                return superClass.getDeclaredField(filedName);
            } catch (NoSuchFieldException e) {
//Field 不在当前类定义, 继续向上转型
            }
        }
        return null;
    }


    /**
     * 使 filed 变为可访问
     * @param field
     */
    public static void makeAccessible(Field field){
        if(!Modifier.isPublic(field.getModifiers())){
            field.setAccessible(true);
        }
    }
}
