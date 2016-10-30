package yong.tank.Game;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import yong.tank.R;
import yong.tank.Result.view.ResultActivity;

/**
 * Created by hasee on 2016/10/30.
 */

public class GameActivity extends Activity {
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
        setContentView(R.layout.activity_game); //一般放在super后面比较好,但是这里，因为要横屏，所以放在后面
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Toast.makeText(this, "event.getX() "+event.getX(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ResultActivity.class);
        this.startActivity(intent);
        return false;
    }
}
