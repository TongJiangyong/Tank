package yong.tank.LocalRecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by hasee on 2016/12/6.
 */

public class LocalRecord<T> {


    public boolean saveInfoLocal(T object,File infoFile) {
        //使用外部存储空间的根目录
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        if (!infoFile.exists()) {
            try {
                infoFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return  false;
            }
        }
        try {
            //openFileOutput只能对activity有效
            fos = new FileOutputStream(infoFile);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
            //成功存储对象
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return  false;
            //这里是保存文件产生异常
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    //fos流关闭异常
                    e.printStackTrace();
                    return  false;
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    //oos流关闭异常
                    e.printStackTrace();
                    return  false;
                }
            }
        }
    }

    public T readInfoLocal(File infoFile) {
        //使用外部存储空间的根目录
        FileInputStream fos = null;
        ObjectInputStream oos = null;
        T object = null;
        if (!infoFile.exists()) {
            try {
                infoFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        try {
            //openFileOutput只能对activity有效
            fos = new FileInputStream(infoFile);
            oos = new ObjectInputStream(fos);
            object = (T)oos.readObject();
            return object;
            //成功存储对象
        } catch (Exception e) {
            e.printStackTrace();
            return null;
            //这里是保存文件产生异常
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    //fos流关闭异常
                    e.printStackTrace();
                    return null;
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    //oos流关闭异常
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }


}
