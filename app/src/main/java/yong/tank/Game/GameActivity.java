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

import yong.tank.Dto.GameDto;
import yong.tank.Game.View.BloodView;
import yong.tank.Game.View.GameView;
import yong.tank.Game.View.PlayerView;
import yong.tank.Game.View.ViewBase;
import yong.tank.Game.control.GameControler;
import yong.tank.Game.control.PlayControler;
import yong.tank.Game.service.GameService;
import yong.tank.R;
import yong.tank.modal.Blood;
import yong.tank.modal.MyTank;
import yong.tank.modal.PlayerPain;
import yong.tank.tool.StaticVariable;
import yong.tank.tool.Tool;

/**
 * Created by hasee on 2016/10/30.
 */

public class GameActivity extends Activity {
    private int tankType ;
    private int mapType ;
    private static String TAG = "GameActivity";
    private PlayControler playControler;
    private GameControler gameControler;
    private GameService gameService;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
        setContentView(R.layout.activity_game); //一般放在super后面比较好,但是这里，因为要横屏，所以放在后面
        RelativeLayout activity_game = (RelativeLayout) findViewById(R.id.activity_game);
        GameDto gameDto = new GameDto();
        tankType=this.getIntent().getIntExtra("tankType", 0);
        mapType=this.getIntent().getIntExtra("mapType", 0);
        //TODO 测试tank
        MyTank myTank =initTank(tankType);
        gameDto.setMyTank(myTank);
        //TODO 测试玩家控制图标
        PlayerPain playerPain = new PlayerPain();
        gameDto.setMyTank(myTank);
        gameDto.setPlayerPain(playerPain);
        //TODO 测试装载血条的视图
        Blood blood = initBlood(true);
        gameDto.setBlood(blood);
        //TODO 考虑可否用反射处理这个问题
        //绘制gameView
        ViewBase gameView =new GameView(this,gameDto);
        gameView.setZOrderOnTop(true); //设置canves为透明必须要加.....
        //绘制playerView
        ViewBase playerView =new PlayerView(this,gameDto);
        playerView.setZOrderOnTop(true); //设置canves为透明必须要加.....
        //绘制bloodView
        ViewBase bloodView =new BloodView(this,gameDto);
        bloodView.setZOrderOnTop(true); //设置canves为透明必须要加.....
        activity_game.addView(gameView);
        activity_game.addView(playerView);
        activity_game.addView(bloodView);
        /*********程序控制器**********/
        gameService = new GameService(gameDto);
        gameControler = new GameControler(gameService);
        /*********设置玩家控制器**********/
        playControler = new PlayControler(this,gameDto,gameControler);
        //TODO 所有初始化等工作完成以后，就开始游戏：
        gameControler.startGame();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Toast.makeText(this, "event.getX() "+event.getX(), Toast.LENGTH_SHORT).show();
        playControler.setMotion(event);
        return false;
    }

    private MyTank initTank(int tankType){
        Bitmap tankPicture_temp = BitmapFactory.decodeResource(getResources(), StaticVariable.TANKBASCINFO[tankType].getPicture());
        Bitmap tankPicture = Tool.reBuildImg(tankPicture_temp,0,1,1,false,true);
        Bitmap armPicture = BitmapFactory.decodeResource(getResources(), R.mipmap.gun);
        MyTank tank = new MyTank(tankPicture,armPicture,tankType, StaticVariable.TANKBASCINFO[tankType]);
        return tank;
    }
    private Blood initBlood(Boolean isMyBlood){
        //TODO 如果是true会找其他图片
        //TODO 这里暂时先别做.....以后再改....
        Bitmap blood_picture=null;
        blood_picture = BitmapFactory.decodeResource(getResources(), StaticVariable.BLOOD);
        Bitmap power_picture=null;
        power_picture = BitmapFactory.decodeResource(getResources(), StaticVariable.POWERR);
        Bitmap bloodBlock_picture=null;
        bloodBlock_picture = BitmapFactory.decodeResource(getResources(), StaticVariable.BLOODBLOCK);
        Blood blood = new Blood(blood_picture, power_picture, bloodBlock_picture,(float)0.7,(float)0.7);
        return blood;
    }

}
