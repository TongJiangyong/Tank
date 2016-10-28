package yong.tank.SelectRoom.View;

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
import yong.tank.SelectRoom.present.SelectPresent;
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
    private int gameMode=1;
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
        gameMode=this.getIntent().getIntExtra("type", StaticVariable.GAMEMODE[0]);
        initButton();
    }
    public void initButton(){
        confirmButton=(Button)findViewById(R.id.confirmButton);
        backTitle=(Button)findViewById(R.id.backTitle);
        backTitle.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
        switch(gameMode){
            case 1:
                    //人机
                showToast("人机");
                break;
            case 2:
                //蓝牙
                break;
            case 3:
                //联网
                break;
        }
    }

    //在这里判断并处理.....
    @Override
    public boolean onTouchEvent(MotionEvent event) {
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
                this.finish();
                break;
            case R.id.confirmButton:
                selectPresent.returnToTitle();
                showToast("go to game");
                break;
        }

    }
}
