package org.violetime.autopers.session.objects;

import org.violetime.autopers.database.DataBaseColumn;
import org.violetime.autopers.database.DataBaseFactory;
import org.violetime.autopers.database.DataBaseSource;
import org.violetime.autopers.platform.AutopersPlatformInvoke;
import org.violetime.autopers.session.AutopersSession;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CopyTableStructure implements AutopersPlatformInvoke {
    private AutopersSession session;
    private Object[] args;
    private DataBaseSource baseSource;
    @Override
    public Object invoke() throws Throwable {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub
        PreparedStatement preparedStatement=null;
        String newTable=(String) args[0];
        String table=(String) args[1];
        try{
            String sql="create table "+newTable+" like "+table;
            preparedStatement= session.getPreparedStatement();
            preparedStatement.execute(sql);
            preparedStatement.close();
            return null;
        }catch(Exception  e){
            System.out.println(e.getMessage());
            try {
                preparedStatement.close();
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                //e1.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public String throwablePrint() {
        return null;
    }
}
