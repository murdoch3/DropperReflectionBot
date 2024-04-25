package org.example;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtils {
    public static Field getField(Class<?> cl, String fieldName) throws NoSuchFieldException {
        Field field = cl.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    public static void setField(Class<?> cl, String fieldName, Object object, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = getField(cl, fieldName);
        field.set(object, value);
    }

    public static Object invokeMethod(Class<?> cl, String methodName, Class<?>[] parameterTypes, Object object, Object[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = cl.getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(object, args);
    }
}
