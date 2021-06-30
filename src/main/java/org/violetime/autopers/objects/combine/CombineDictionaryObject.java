package org.violetime.autopers.objects.combine;

import java.lang.reflect.Method;

public class CombineDictionaryObject {
   private String field;
   private Method query;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Method getQuery() {
        return query;
    }

    public void setQuery(Method query) {
        this.query = query;
    }
}
