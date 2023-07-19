package com.homework;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class sqlConn {
    private String url;
    private String id;
    private String pw;
    private Connection dbConn;
    public sqlConn(String u, String i, String p){
        this.url = u;
        this.id = i;
        this.pw = p;
    }
    public void setDbConn(){
        try
        {
            dbConn = DriverManager.getConnection(url, id, pw);
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
                System.out.println("DB Close [성공]");
            }
        }
        catch (SQLException e)
        {
            System.out.println("DB Close [실패]");
            e.printStackTrace();
        }
    }
    public List<Object> selectAddress(String addr){
        PreparedStatement st;
        List<Object> resultList = new ArrayList<Object>();
        int count = 0;
        try{
            st = dbConn.prepareStatement("select * from Address where 도로명주소 = ?");
            st.setString(1, addr);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                count += 1;
                Map<String, String> tempMap = new HashMap<String, String>();
                for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                    tempMap.put(rs.getMetaData().getColumnName(i+1), rs.getString(rs.getMetaData().getColumnName(i+1)));
                }
                System.out.print(count);
                System.out.println(tempMap);
                resultList.add(tempMap);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }

        return resultList;
    }

}
