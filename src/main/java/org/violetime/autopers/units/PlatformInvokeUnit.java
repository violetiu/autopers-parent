package org.violetime.autopers.units;

import org.violetime.autopers.platform.AutopersPlatformInvoke;

import java.lang.reflect.Field;

public class PlatformInvokeUnit {
    public static void setField(Class<?> invokeClass, AutopersPlatformInvoke platformInvoke, Field field, Object object)
            throws Exception {
        field.setAccessible(true);
        field.set(platformInvoke, object);
        field.setAccessible(false);
    }
}
