package yong.tank.Game.thread;

import android.util.Log;

import yong.tank.modal.Bullet;

/**
 * Created by hasee on 2016/11/8.
 */

public class ExplodeThread implements Runnable{
    private Bullet bullets;
    private boolean flag =true;
    private static String TAG = "ExplodeThread";
    @Override
    public void run() {
        while(flag){
            Log.w(TAG,"explodeTest");
            //在这个爆炸线程中，绘制爆炸的方法为：
            //在这里获取所有的爆炸点list
            //遍历每一个爆炸点，进行绘制
            //在每一个点的绘制方法中，绘制当前的一幅图像，并让图像+1
            //绘制完以后，检查这个点是否绘制完全，如果绘制完全，则将这个点从list中删除即可
            //增加的地方有很多个，但是删除的地方只有这一个即可......
            //每一个爆炸点需要的属性： 位置（x,y）,爆炸图像等....



            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    public void stopThread(){
        flag = false;
    }
}
