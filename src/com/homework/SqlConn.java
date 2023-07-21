package com.homework;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class SqlConn {
    private final String  url;
    private Connection dbConn;
    public SqlConn(String u){
        this.url = u;
    }
    public void setDbConn(){
        try
        {
            dbConn = DriverManager.getConnection(url);
            System.out.println("DB Connection [성공]");
        }
        catch (SQLException e)
        {
            System.out.println("DB Connection [실패]");
            e.printStackTrace();
        }
    }
    public void closeDbConn(){
        try
        {
            if(dbConn != null)
            {
                dbConn.close();
                dbConn = null;
                //System.out.println("DB Close [성공]");
            }
        }
        catch (SQLException e)
        {
            System.out.println("DB Close [실패]");
            e.printStackTrace();
        }
    }
    public List<Map> selectAddress(String addr){
        PreparedStatement st;
        List<Map> resultList = new ArrayList<>();
        try{
            st = dbConn.prepareStatement("select * from Address_2023 where 도로명주소 = ?");
            st.setString(1, addr);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Map<String, String> tempMap = new HashMap<>();
                for (int i = 0; i < rs.getMetaData().getColumnCount(); i++)
                    tempMap.put(rs.getMetaData().getColumnName(i + 1), rs.getString(rs.getMetaData().getColumnName(i + 1)));
                resultList.add(tempMap);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return resultList;
    }

}
