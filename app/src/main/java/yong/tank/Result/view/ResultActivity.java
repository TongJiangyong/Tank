package yong.tank.Result.view;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import yong.tank.R;
import yong.tank.Title.View.MainActivity;

/**
 * Created by hasee on 2016/10/30.
 */

public class ResultActivity extends Activity implements View.OnClickListener{
    private Button againButton;
    private Button returnTitleButton;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
        setContentView(R.layout.activity_result); //一般放在super后面比较好,但是这里，因为要横屏，所以放在后面
        againButton=(Button)findViewById(R.id.againButton);
        returnTitleButton=(Button)findViewById(R.id.returnTitleButton);
        againButton.setOnClickListener(this);
        returnTitleButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.againButton:
                Toast.makeText(this, "againButton", Toast.LENGTH_SHORT).show();
                break;
            case R.id.returnTitleButton:
                Intent intent = new Intent(this, MainActivity.class);
                this.startActivity(intent);
                break;
        }
    }
}
