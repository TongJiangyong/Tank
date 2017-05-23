package yong.tank.Result_Activity.view;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import yong.tank.R;
import yong.tank.Title_Activity.View.MainActivity;
import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/10/30.
 */

public class ResultActivity extends Activity implements View.OnClickListener{
/*    private Button againButton;*/
    private Button returnTitleButton;
    private RelativeLayout relativeLayout;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
        setContentView(R.layout.activity_result); //一般放在super后面比较好,但是这里，因为要横屏，所以放在后面
        //注意setContentView的这种写法，不能直接写setContentView(relativeLayout),因为使用findViewById不算实例化.....
        relativeLayout = (RelativeLayout)findViewById(R.id.activity_result);
        if(this.getIntent().getIntExtra("gameResult", 0)== StaticVariable.GAME_WIN){
            showToast("恭喜您击败对手，获得胜利！");
            relativeLayout.setBackgroundResource(R.mipmap.bg_gamevictory);
        }else{
            showToast("您被击败了，努力再练练吧 ^_^ ");
            relativeLayout.setBackgroundResource(R.mipmap.bg_gameover);
        }
/*        againButton=(Button)findViewById(R.id.againButton);*/
        returnTitleButton=(Button)findViewById(R.id.returnTitleButton);
/*        againButton.setOnClickListener(this);*/
        returnTitleButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
/*            case R.id.againButton:
                this.showToast("againButton" );
                break;*/
            case R.id.returnTitleButton:
                Intent intent = new Intent(this, MainActivity.class);
                this.startActivity(intent);
                break;
        }
    }

    public void showToast(String info){
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
    }
}
