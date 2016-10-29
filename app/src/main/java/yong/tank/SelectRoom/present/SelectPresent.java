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
        for(int i=0;i<selectView.getMapPictures().length;i++)
        {
            if(selectView.getMapPictures()[i].isContainPoint((int)event.getX(),(int)event.getY())){
                //Toast.makeText(this.context, "picture", Toast.LENGTH_SHORT).show();
                this.showTankSelected(i);
            }
        }
            for(int j =0;j<selectView.getTankPictures().length;j++){
            if(selectView.getTankPictures()[j].isContainPoint((int)event.getX(),(int)event.getY())){
                this.showMapSelected(j);
            }
        }
    }
    public void showTankSelected(int i){
        selectView.showTankSelected(i);

    }
    public void showMapSelected(int i){
        selectView.showMapSelected(i);
    }
}
