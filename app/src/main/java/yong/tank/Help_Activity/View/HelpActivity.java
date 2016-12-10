package yong.tank.Help_Activity.View;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import yong.tank.R;
import yong.tank.Title_Activity.View.MainActivity;
import yong.tank.tool.StaticVariable;
import yong.tank.tool.Tool;

/**
 * Created by hasee on 2016/10/28.
 * activity只有经过设置(layout中)才能浮在上面，这个感觉要设置为比较好fragment比较好....这种做法太差....
 * 这里的help暂定都是静态的页面，所以没有采用mvp的写法....
 */

public class HelpActivity extends Activity implements View.OnClickListener{
    private Bitmap help_bg;    //帮助背景
    private Button directionButton_right;  //方向右按钮
    private Button directionButton_left;  //方向左按钮
    private Button backButton;   //返回标题按钮
    private TextView textView;
    private static String TAG = "HelpActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
        setContentView(R.layout.help_layout);
        RelativeLayout help_layout = (RelativeLayout) findViewById(R.id.help_layout);
        //这里是设置图片的部分
        ImageView helpBg = (ImageView) findViewById(R.id.help_bg);
        //设置背景图片，因为要进行拉伸没办法，必须要这么做.....
        Bitmap help_bg_temp  = BitmapFactory.decodeResource(getResources(),R.mipmap.callboard);
        //TODO 将图像放大N倍的硬编码,不知道为啥，这里的图片设置，很怪异,大概是这个浮动的影响，而且经常报错误out of memery
        //float size = (float) ((float)(StaticVariable.LOCAL_SCREEN_WIDTH)/(float)help_bg_temp.getWidth()*2.5);
        //Log.w(TAG,"LOCAL_SCREEN_WIDTH:"+StaticVariable.LOCAL_SCREEN_WIDTH +"  help_bg_temp:"+help_bg_temp.getWidth()+"  size:"+size +"  help_bg_temp_h:"+help_bg_temp.getHeight());
        help_bg = Tool.reBuildImg(help_bg_temp,0,7,7,false,false);
        //Log.w(TAG,"help_bg_temp:"+help_bg.getWidth());
        Tool.releaseBitmap(help_bg_temp);
        helpBg.setBackgroundDrawable(new BitmapDrawable(help_bg));
        //设置两个箭头按钮
        directionButton_right = (Button)findViewById(R.id.arrowRightButton);
        directionButton_left = (Button)findViewById(R.id.arrowLeftButton);
        //directionButton_right.setBackgroundDrawable(new BitmapDrawable(toDescribeButton_bg));
        directionButton_right.setOnClickListener(this);
        directionButton_left.setOnClickListener(this);
        directionButton_left.setVisibility(View.INVISIBLE);
        //这里设置返回按钮
        backButton = (Button)findViewById(R.id.help_backTitle);
        backButton.setOnClickListener(this);
        //帮助信息
        textView = (TextView)findViewById(R.id.helpText);
        textView.setText(StaticVariable.HELPINFO);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.help_backTitle:
                Intent intent = new Intent(this,MainActivity.class);
                this.startActivity(intent);
                break;
            case R.id.arrowRightButton:
                directionButton_right.setVisibility(View.INVISIBLE);
                directionButton_left.setVisibility(View.VISIBLE);
                textView.setText(StaticVariable.STATEMENTINFO);
                break;
            case R.id.arrowLeftButton:
                directionButton_left.setVisibility(View.INVISIBLE);
                directionButton_right.setVisibility(View.VISIBLE);
                textView.setText(StaticVariable.HELPINFO);
                break;
        }

    }
    @Override
    public void onPause(){
        super.onPause();
        Tool.releaseBitmap(help_bg);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Tool.releaseBitmap(help_bg);
    }

}
