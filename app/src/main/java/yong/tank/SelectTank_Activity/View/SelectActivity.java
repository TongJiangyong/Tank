package yong.tank.SelectTank_Activity.View;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import yong.tank.R;
import yong.tank.SelectTank_Activity.present.SelectPresent;
import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/10/27.
 */

//TODO 做一些简单的绘制工作，测试绘制性能，并引入图像工厂
public class SelectActivity extends Activity implements View.OnClickListener{
    private SelectView selectView;
    private SelectPresent selectPresent;
    private Button confirmButton;
    private Button backTitle;
    //这个mode只是测试用的，无实际意义
    private int gameMode=0;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
        setContentView(R.layout.tank_select); //一般放在super后面比较好,但是这里，因为要横屏，所以放在后面
        RelativeLayout selectLayout = (RelativeLayout)findViewById(R.id.selectLayout);
        selectView = new SelectView(this); //这里是加载其他界面
        selectLayout.addView(selectView);
        selectPresent= new SelectPresent(this,selectView,this);
        gameMode=this.getIntent().getIntExtra("type", StaticVariable.GAMEMODE[0]); //默认值为本地模式而已....
        initButton();
    }
    public void initButton(){
        confirmButton=(Button)findViewById(R.id.confirmButton);
        backTitle=(Button)findViewById(R.id.backTitle);
        backTitle.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
        switch(gameMode){
            case 0:
                    //人机
                showToast("人机");
                break;
            case 2:
                //蓝牙
                showToast("蓝牙");
                break;
            case 1:
                //联网
                if(StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.ACTIVITY){
                    confirmButton.setText("创建房间");
                }else{
                    confirmButton.setText("加入房间");
                }
                showToast("联网");
                break;
        }
    }

    //在这里判断并处理.....
    @Override
    public boolean onTouchEvent(MotionEvent event) {
       // Toast.makeText(this, "event.getX() "+event.getX(), Toast.LENGTH_SHORT).show();
        selectPresent.setSelect(event);
        return false;
    }

    //这里是进行逻辑判断
    public void showToast(String info){
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.backTitle:
                selectPresent.returnToTitle();
                break;
            case R.id.confirmButton:
                selectPresent.gotoBattle();
                    //TODO 进入战场后，毁掉自身.....
                break;
        }

    }


}
