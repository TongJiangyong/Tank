package yong.tank.Dto;

import java.io.Serializable;

/**
 * Created by hasee on 2016/11/27.
 */

public class testDto implements Serializable {
    public int id ;
    public String name;

    public testDto(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "testDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
