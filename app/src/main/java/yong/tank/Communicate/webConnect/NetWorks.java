package yong.tank.Communicate.webConnect;

import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hasee on 2016/12/6.
 * 完成对retrofit的封装，并在这里使用到Rxjava，封装使用即可.......
 */

public class NetWorks extends RetrofitUtils {

    //创建实现接口调用
    protected static final NetService service = getRetrofit().create(NetService.class);

    //设缓存有效期为1天
    protected static final long CACHE_STALE_SEC = 60 * 60 * 24 * 1;
    //查询缓存的Cache-Control设置，使用缓存
    protected static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_SEC;
    //查询网络的Cache-Control设置。不使用缓存
    protected static final String CACHE_CONTROL_NETWORK = "max-age=0";

    private interface NetService {

        //定义web接口的地方
        @POST("WebService")
        Observable<String> connectedTest(@Query("action") String action);

        @POST("WebService")
        Observable<String> getUserInfo(@Query("action") String action,@Query("userId") int userId);

        @POST("WebService")
        Observable<String> addNewUser(@Query("action") String action,@Query("userInfo") String userInfo);

        @POST("WebService")
        Observable<String> userLogin(@Query("action") String action,@Query("userInfo") String userInfo);

        @POST("WebService")
        Observable<String> getRoomList(@Query("action") String action,@Query("userId") int state);
    }

    //测试连接状态
    public static void connectTest(String action,Observer<String> observer){
        setSubscribe(service.connectedTest(action),observer);
    }

    //获取单个用户信息
    public static void getUserInfo(String action,int userId,Observer<String> observer){
        setSubscribe(service.getUserInfo(action,userId),observer);
    }

    //用户登录
    public static void userLogin(String action, String userInfo,Observer<String> observer){
        setSubscribe(service.userLogin(action,userInfo),observer);
    }

    //用户注册
    public static void addNewUser(String action, String userInfo,Observer<String> observer){
        setSubscribe(service.addNewUser(action,userInfo),observer);
    }

    //获取所有房间信息
    public static void getRoomList(String action, int state,Observer<String> observer){
        setSubscribe(service.getRoomList(action,state),observer);
    }





    //以后讨论一下这里定义为静态的是否可行.....这里就暂时不处理了.....
    public static <T> void setSubscribe(Observable<T> observable, Observer<T> observer) {
        observable.subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())//子线程访问网络
                .observeOn(AndroidSchedulers.mainThread())//回调到主线程
                .subscribe(observer);
    }

}
