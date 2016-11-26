package yong.tank.Communicate.ComData;

/**
 * Created by hasee on 2016/11/26.
 */

/**
 * DATA的包装为：
 * flag------标记传给谁 格式为 id+#
 * command-------标记命令
 * Object封装的格式
 *      响应command的各种定义好的参数
 *      封装的obj即可......但是这里，好像解析成object后，就不好再打包了
 * **/
public class ComDataF {
    private String flag;//服务器端采用flag+object的形式做简化处理  0#自己的id/给服务器    123456#/广播
    private ComDataF comDataF;

    public ComDataF(String flag, ComDataF comDataF) {
        this.flag = flag;
        this.comDataF = comDataF;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public ComDataF getComDataF() {
        return comDataF;
    }

    public void setComDataF(ComDataF comDataF) {
        this.comDataF = comDataF;
    }
}
