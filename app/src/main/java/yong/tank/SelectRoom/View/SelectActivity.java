package yong.tank.SelectRoom.View;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import yong.tank.R;

/**
 * Created by hasee on 2016/10/27.
 */

//TODO 做一些简单的绘制工作，测试绘制性能，并引入图像工厂
public class SelectActivity extends Activity implements View.OnClickListener{
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
        setContentView(R.layout.tank_select);
    }


    @Override
    public void onClick(View view) {

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        Toast.makeText(this, "test onTouch", Toast.LENGTH_SHORT).show();
        return true;//这里不论返回true或者false，都不再做处理了
    }
}
