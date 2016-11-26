package yong.tank.Communicate.ComData;

/**
 * Created by hasee on 2016/11/26.
 */

public class ComDataS {
    private String commad;
    private Object object;

    public ComDataS(String commad, Object object) {
        this.commad = commad;
        this.object = object;
    }

    public String getCommad() {
        return commad;
    }

    public void setCommad(String commad) {
        this.commad = commad;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
