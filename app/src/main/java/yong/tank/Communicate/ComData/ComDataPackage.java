package yong.tank.Communicate.ComData;

import com.google.gson.Gson;

/**
 * Created by hasee on 2016/11/26.
 * 处理传输输入的工具类
 */

public class ComDataPackage {
    private static Gson gson = new Gson();
    public static ComDataS packageToS (String commad,Object info){
       return new ComDataS(commad,info);
    }

    public static ComDataS packageToF (String flag,ComDataF comDataF){
        return new ComDataS(flag,comDataF);
    }

    public static ComDataF packageToF (String flag,String commad,Object info){
        return new ComDataF(flag,packageToS(commad,info));
    }

    public static ComDataF unpackToF(String msg){
        return gson.fromJson(msg,ComDataF.class);
    }
}
