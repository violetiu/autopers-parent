package org.violetime.autopers.query;

public class AutopersQueryMethods {
    private String method;

    public AutopersQueryMethods(String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return method;
    }

    @Override
    public boolean equals(Object obj) {
        return method.equals(obj.toString());
    }
    public  static AutopersQueryMethods Equals=new AutopersQueryMethods("_Equals");
    public  static AutopersQueryMethods Like=new AutopersQueryMethods("_Like");
    public  static AutopersQueryMethods In=new AutopersQueryMethods("_In");
    public  static AutopersQueryMethods LikeEnd=new AutopersQueryMethods("_LikeEnd");

}
