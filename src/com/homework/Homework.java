package com.homework;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class Homework {
    public static Boolean findSQL(SqlConn sql, String newAddr, int j, int idx){
        List<Map> result;
        result = sql.selectAddress(newAddr.substring(j, idx)); //전체 문자열에서 j부터 idx까지 문자열을 탐색 시도
        if(!result.isEmpty()){ //정확히 일치하는 결과값이 존재한다면
            //System.out.println(result);
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
    public static ArrayList<Integer> findIdx(String addr){ //로, 길에 해당하는 index 찾기, 가장 뒤에 있는 글자 부터 찾기시작
        ArrayList<Integer> result = new ArrayList<>();
        while(addr.length() != 0){
            int roLastIdx = addr.lastIndexOf("로");
            int gilLastIdx = addr.lastIndexOf("길");
            if(roLastIdx > gilLastIdx) { //로가 뒤에 있으면 roLastIdx를 추가하고 로 앞까지 문자열 자르기
                result.add(roLastIdx);
                addr = addr.substring(0, roLastIdx);
            }
            else if(roLastIdx < gilLastIdx){ //길이 뒤에 있으면 gilLastIdx를 추가하고 길 앞까지 문자열 자르기
                result.add(gilLastIdx);
                addr = addr.substring(0, gilLastIdx);
            }
            else{ //만약 길과 로가 같이 있다면 문자열에 더이상 길, 로가 존재하지 않음
                break;
            }
        }
        return result;
    }
    public static Map findRegion(List<Map> resultList, String addr){
        if(resultList.size() == 1)
            return resultList.get(0);
        for(int j=addr.length()-1; j != -1; j--){
            //System.out.println(result);
            if(resultList.size() == 1)
                break;
            List<Map> tempList = new ArrayList<>(resultList);
            //System.out.println(addr.substring(j, j+1));
            for (Object o : resultList) {
                Map<String, String> temp = (Map) o;
                if (!temp.get("광역자치단체").contains(addr.substring(j, j + 1)) && !temp.get("기초자치단체").contains(addr.substring(j, j + 1))) {
                    //=> 뒤에서 부터 한글자씩 파싱하여 기초자치단체 혹은 광역자치단체에 글자가 없다면 결과리스트에서 제거
                    tempList.remove(temp);
                }
            }
            resultList = new ArrayList<>(tempList);
        }
        return resultList.isEmpty() ? null : resultList.get(0);
    }
    public static void main(String[] args){
        Scanner s = new Scanner(System.in);
        final String dbURL = "jdbc:sqlite:address.db"; //내재된 SQLite 사용
        SqlConn sql = new SqlConn(dbURL);
        sql.setDbConn();   //DB 연결 수립

        System.out.println("주소를 입력해주세요");
        String addr = s.nextLine(); //주소 입력 시작

        String newAddr = addr.replaceAll("[^가-힣A-Za-z·\\d~.]", ""); //한글 낯자, . · 제외 특수문자 제거
        newAddr = newAddr.toUpperCase(); //"APEC로" 검색을 위한 대문자화
        ArrayList<Integer> idxList = findIdx(newAddr); //로, 길에 대한 index 탐색
        //System.out.println(newAddr);

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
