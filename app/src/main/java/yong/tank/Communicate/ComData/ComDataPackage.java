package yong.tank.Communicate.ComData;

/**
 * Created by hasee on 2016/11/26.
 * 处理传输输入的工具类
 */

public class ComDataPackage {
    public static ComDataS packageToS (String commad,Object info){
       return new ComDataS(commad,info);
    }

    public static ComDataS packageToF (String flag,ComDataF comDataF){
        return new ComDataS(flag,comDataF);
    }

    public static ComDataS packageToF (String flag,String commad,Object info){
        return new ComDataS(flag,packageToS(commad,info));
    }

}
