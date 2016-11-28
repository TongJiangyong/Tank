package yong.tank.Communicate.ComData;

import java.io.Serializable;

/**
 * Created by hasee on 2016/11/26.
 */

public class ComDataS implements Serializable {
    private String commad;
    private String object;

    public ComDataS(String commad, String object) {
        this.commad = commad;
        this.object = object;
    }

    public String getCommad() {
        return commad;
    }

    public void setCommad(String commad) {
        this.commad = commad;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }
}
