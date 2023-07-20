package com.homework;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class Homework {
    public static Boolean findSQL(SqlConn sql, String newAddr, int j, int idx){
        List<Object> result;
        result = sql.selectAddress(newAddr.substring(j, idx));
        if(!result.isEmpty()){
            System.out.println(result);
            Map<String, String> temp = findRegion(result, newAddr.substring(0, j));
            if(temp == null){
                System.out.println("옳바르지 않은 주소 입니다.");
                sql.closeDbConn();
                return true;
            }
            System.out.println(safeGet(temp.get("광역자치단체"))+" "+safeGet(temp.get("기초자치단체"))+" "+safeGet(temp.get("도로명주소")));
            return true;
        }
        return false;
    }
    public static String safeGet(String s) {
        return s == null ? "" : s;
    }
    public static ArrayList<Integer> findIdx(String addr){
        ArrayList<Integer> result = new ArrayList<Integer>();
        while(addr.length() != 0){
            int roLastIdx = addr.lastIndexOf("로");
            int gilLastIdx = addr.lastIndexOf("길");
            if(roLastIdx > gilLastIdx) {
                result.add(roLastIdx);
                addr = addr.substring(0, roLastIdx);
            }
            else if(roLastIdx < gilLastIdx){
                result.add(gilLastIdx);
                addr = addr.substring(0, gilLastIdx);
            }
            else{
                break;
            }
        }
        return result;
    }
    public static Map<String, String> findRegion(List<Object> resultList, String addr){
        if(resultList.size() == 1)
            return (Map)resultList.get(0);
        for(int j=addr.length()-1; j != -1; j--){
            //System.out.println(result);
            if(resultList.size() == 1)
                break;
            List<Object> tempList = new ArrayList<Object>(resultList);
            //System.out.println(addr.substring(j, j+1)); => 한글자씩 파싱하여 기초자치단체 혹은 광역자치단체에 있는지 확인
            for (Object o : resultList) {
                Map<String, String> temp = (Map) o;
                if (!temp.get("광역자치단체").contains(addr.substring(j, j + 1)) && !temp.get("기초자치단체").contains(addr.substring(j, j + 1))) {
                    tempList.remove(temp);
                }
            }
            resultList = new ArrayList<Object>(tempList);
        }
        return resultList.isEmpty() ? null : (Map)resultList.get(0);
    }
    public static void main(String args[]){
        Scanner s = new Scanner(System.in);
        final String dbURL = "jdbc:sqlite:address.db";
        SqlConn sql = new SqlConn(dbURL);
        sql.setDbConn();

        System.out.println("주소를 입력해주세요");
        String addr = s.nextLine();

        String newAddr = addr.replaceAll("[^가-힣A-Za-z·\\d~\\.]", "");
        newAddr = newAddr.toUpperCase();
        ArrayList<Integer> idxList = findIdx(newAddr);
        System.out.println(newAddr);
        //System.out.println(findIdx(newAddr));
        //System.out.println(idxList);
        for(int i = 0; i!= idxList.size(); i++){
            for(int j=0; j<idxList.get(i); j++){
                System.out.println(newAddr.substring(j, idxList.get(i)+1));
                if(findSQL(sql, newAddr, j, idxList.get(i)+1)){
                    sql.closeDbConn();
                    return;
                }
            }
        }
    }
}
