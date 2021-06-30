package org.violetime.autopers.query;

import java.util.ArrayList;
import java.util.List;

public class QueryObjectReturnData {
    private String sql;
    private List<Object> params;

    public QueryObjectReturnData(String sql, Object param) {
        this.sql = sql;
        this.params = new ArrayList<>();
        this.params.add(param);
    }
    public QueryObjectReturnData() {

    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<Object> getParams() {
        return params;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }
}
