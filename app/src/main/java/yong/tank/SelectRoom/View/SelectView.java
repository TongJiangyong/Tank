package yong.tank.SelectRoom.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;
import android.widget.Toast;

import yong.tank.R;

/**
 * Created by hasee on 2016/10/27.
 */

public class SelectView extends View {
    private Context context;
    private Bitmap[] tankPicture;//tank的图片
    private Bitmap bitmapTest;
    public SelectView(Context context) {
        super(context);
        this.context=context;
        //TODO 这里用来初始化加载一些图片，暂时不用工具类，采用在使用的地方加载图片的方法
        bitmapTest = BitmapFactory.decodeResource(getResources(), R.mipmap.tank_1);
    }

    //TODO 者利用来绘制图片
    protected void onDraw(Canvas canvas) {
        Toast.makeText(this.context, "test", Toast.LENGTH_SHORT).show();
        canvas.drawBitmap(bitmapTest, 0, 0, null);// 绘制背景
    }
    //这里是进行逻辑判断


   /* //这里用来判断，点击是否在图像范围内
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.v);
        canvas.drawBitmap(bitmap, 0, 0, null);

        //创建和位图一样位置的Rect
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        if(rect.contains((int)x, (int)y)){
            System.out.println("范围之内");
        }
        else{
            System.out.println("范围之外");
        }test onTouch度：" + bitmap.getHeight());
        System.out.println("点击X：" + x + "点击Y：" + y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            x = event.getX();
            y = event.getY();
            // 重绘
            invalidate();
        }
        return true;
    }*/
}
