
package yong.tank.Title_Activity.presenter;

import android.content.Context;

import yong.tank.Title_Activity.View.WebInfoActivity;

/**
 * Created by hasee on 2016/10/27.
 * 处理Web相关的方法
 */

public class WebInfoPresenter {
    private static String TAG = "WebInfoPresenter";
    private Context context;
    private WebInfoActivity webInfoActivity;
    public WebInfoPresenter(Context context, WebInfoActivity webInfoActivity){
        this.webInfoActivity=webInfoActivity;
        this.context=context;
    }
}
