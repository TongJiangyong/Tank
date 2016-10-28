package yong.tank.SelectRoom.present;

import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;

import yong.tank.SelectRoom.View.SelectActivity;
import yong.tank.SelectRoom.View.SelectView;
import yong.tank.Title.View.MainActivity;

/**
 * Created by hasee on 2016/10/28.
 */

public class SelectPresent {
    private SelectView selectView;
    private Context context;
    private SelectActivity SelectActivity;
    //TODO 定义一个最好去定义一个view，而不是这个.....
    public SelectPresent(Context context, SelectView selectView, SelectActivity SelectActivity){
        this.context= context;
        this.selectView=selectView;
        this.SelectActivity = SelectActivity;
    }


    public void returnToTitle() {
        Intent intent = new Intent(this.context, MainActivity.class);
        this.context.startActivity(intent);
    }

    public void setSelect(MotionEvent event) {

    }
}
