package yong.tank.SelectRoom.View;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import yong.tank.R;

/**
 * Created by hasee on 2016/10/27.
 */

//TODO 做一些简单的绘制工作，测试绘制性能，并引入图像工厂
public class SelectActivity extends Activity {
    private View selectView;
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
    }

    //在这里判断并处理.....
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    Toast.makeText(this, "x:"+event.getX(), Toast.LENGTH_SHORT).show();
        return false;
    }


}
