package yong.tank.Communicate.ComData;

import com.google.gson.Gson;

import yong.tank.Data.GameSendingData;

/**
 * Created by hasee on 2016/11/26.
 * 处理传输输入的工具类
 */

public class ComDataPackage {
    private static Gson gson = new Gson();
    public static ComDataS packageToS (String commad,String info){
       return new ComDataS(commad,info);
    }

    public static ComDataF packageToF (String flag,ComDataS comDataS){
        return new ComDataF(flag,comDataS);
    }
    public static GameSendingData packageToObject (String info){
        return gson.fromJson(info, GameSendingData.class);
    }

    public static ComDataF packageToF (String flag,String commad,String info){
        return new ComDataF(flag,packageToS(commad,info));
    }

    public static ComDataF unpackToF(String msg){
/*  保留这种泛型的做法，以后学习吧
      Type objectType ;
        if(clazz.equals("String")){
            //objectType = new TypeToken<ComDataF<String>>(){}.getType();
        }else{
            //objectType = new TypeToken<ComDataF<testDto>>(){}.getType();
        }*/
        return gson.fromJson(msg,ComDataF.class);
    }
}
