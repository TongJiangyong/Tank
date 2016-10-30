package yong.tank.SelectTank.present;

import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;

import yong.tank.Game.GameActivity;
import yong.tank.SelectTank.View.SelectActivity;
import yong.tank.SelectTank.View.SelectView;
import yong.tank.Title.View.MainActivity;

/**
 * Created by hasee on 2016/10/28.
 */

public class SelectPresent {
    private SelectView selectView;
    private Context context;
    private SelectActivity SelectActivity;
    private static String TAG ="SelectPresent";
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
                this.showMapSelected(i);
            }
        }
        for(int j =0;j<selectView.getTankPictures().length;j++){
            if(selectView.getTankPictures()[j].isContainPoint((int)event.getX(),(int)event.getY())){
                //Toast.makeText(this.context, "picture——1:"+j, Toast.LENGTH_SHORT).show();
                this.showTankSelected(j);
            }
        }
    }
    public void showTankSelected(int i){
        this.selectView.showTankSelected(i);

    }
    public void showMapSelected(int i){
        this.selectView.showMapSelected(i);
    }

    //进入game界面，并传递参数
    public void gotoBattle() {
        Intent intent = new Intent(this.context, GameActivity.class);
        intent.putExtra("tankType",this.selectView.getSelectedTank());
        intent.putExtra("mapType",this.selectView.getSelectedMap());
        this.context.startActivity(intent);
    }
}
