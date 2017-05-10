package yong.tank.Communicate.InterfaceGroup;

import yong.tank.Communicate.ComData.ComDataF;

/**
 * Created by hasee on 2017/5/9.
 * //本地和server的通信，使用管道还是共享内存实现？？？
 * http://blog.csdn.net/jethai/article/details/52345249
 * 使用管道太过复杂 ，直接使用共享内存即可.....
 * 分析认为，还是通过管道来进行进程之间的通讯逻辑会比较正常，这里采用管道的模式来设置
 */

public interface ServerCommunicate {

    //接收到数据
    void sendDataToActivity(ComDataF comDataF);
    void sendDataToPassive(String msg);
    //发送的数据
    void reciveDataFromActivity(String msg);
    void reciveDataFromPassive(ComDataF comDataF);
}
