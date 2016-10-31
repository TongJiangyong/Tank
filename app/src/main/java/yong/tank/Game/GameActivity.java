package yong.tank.Game;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import yong.tank.Dto.GameDto;
import yong.tank.Game.View.GameView;
import yong.tank.Game.View.PlayerView;
import yong.tank.R;
import yong.tank.modal.MyTank;
import yong.tank.tool.StaticVariable;
import yong.tank.tool.Tool;

/**
 * Created by hasee on 2016/10/30.
 */

public class GameActivity extends Activity {
    private int tankType ;
    private int mapType ;
    private static String TAG = "GameActivity";
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
        setContentView(R.layout.activity_game); //一般放在super后面比较好,但是这里，因为要横屏，所以放在后面
        RelativeLayout activity_game = (RelativeLayout) findViewById(R.id.activity_game);
        GameDto gameDto = new GameDto();
        //TODO 测试tank
        tankType=this.getIntent().getIntExtra("tankType", 0);
        mapType=this.getIntent().getIntExtra("mapType", 0);
        MyTank myTank =initTank(tankType);
        gameDto.setMyTank(myTank);
        //TODO 考虑可否用反射处理这个问题
        GameView gameView =new GameView(this);
        gameView.setGameDto(gameDto); //没办法，要加在后面
        gameView.setZOrderOnTop(true); //设置canves为透明必须要加.....
        PlayerView playerView =new PlayerView(this);
        playerView.setGameDto(gameDto); //没办法，要加在后面
        playerView.setZOrderOnTop(true); //设置canves为透明必须要加.....
        activity_game.addView(gameView);
        activity_game.addView(playerView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Toast.makeText(this, "event.getX() "+event.getX(), Toast.LENGTH_SHORT).show();
        //Intent intent = new Intent(this, ResultActivity.class);
        //this.startActivity(intent);
        return false;
    }

    private MyTank initTank(int tankType){
        Bitmap tankPicture_temp = BitmapFactory.decodeResource(getResources(), StaticVariable.TANKBASCINFO[tankType].getPicture());
        Bitmap tankPicture = Tool.reBuildImg(tankPicture_temp,0,1,1,false,true);
        Bitmap armPicture = BitmapFactory.decodeResource(getResources(), R.mipmap.gun);
        MyTank tank = new MyTank(tankPicture,armPicture,tankType, StaticVariable.TANKBASCINFO[tankType]);
        return tank;
    }

}
