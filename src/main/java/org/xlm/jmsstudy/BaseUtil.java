package org.xlm.jmsstudy;

import java.util.HashMap;

public class BaseUtil {

    private int code;
    private String msg;

    public static HashMap<Object,Object> back(int code, String msg){
        HashMap<Object, Object> map = new HashMap<>();
        map.put("id",code);
        map.put("msg",msg);
        return map;
    }
}
