package yong.tank.Communicate.webConnect;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import yong.tank.modal.TmdbMovieDetail;
import yong.tank.modal.TmdbSearchResult;

/**
 * Created by hasee on 2016/12/6.
 * 完成对retrofit的封装，并在这里使用到Rxjava，封装使用即可.......
 * 在这里设置不同的借口，适应不同的数据选择方法.....暂时用TMDB和豆瓣两个为测试，测试两个 1搜索，2，打印信息即可
 */

public class TmdbScraper extends RetrofitUtils {

    //创建实现接口调用
    protected static final NetService service = getRetrofit().create(NetService.class);

    //设缓存有效期为1天
    protected static final long CACHE_STALE_SEC = 60 * 60 * 24 * 1;
    //查询缓存的Cache-Control设置，使用缓存
    protected static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_SEC;
    //查询网络的Cache-Control设置。不使用缓存
    protected static final String CACHE_CONTROL_NETWORK = "max-age=0";

    private interface NetService {

        //查询影片信息

        /**
         * @param api_key   传入 api-key
         * @param language  传入 选择搜索的语言 ISO 639-1 中文zh  英文 en
         * @param movieName     传入要查询的电影名称  传入名称即可
         * @param include_adult   是否有成人内容 默认为false
         * @param page         获取多少页面数据 默认为1
         * @return
         */
        @GET("search/movie")
        Observable<TmdbSearchResult>searchResult(@Query("api_key")String api_key,@Query("language")String language,@Query("query")String movieName,@Query("include_adult")boolean include_adult,@Query("page")int page);


        //图像地址：
        //https://image.tmdb.org/t/p/w600_and_h900_bestv2/wSJPjqp2AZWQ6REaqkMuXsCIs64.jpg
        //https://image.tmdb.org/t/p/w300_and_h450_bestv2/wSJPjqp2AZWQ6REaqkMuXsCIs64.jpg
        //https://image.tmdb.org/t/p/w132_and_h132_bestv2/mmFfPFw9n3ObTqE3nx20uwpnilI.jpg
        @GET("movie/{id}")
        Observable<TmdbMovieDetail> movieDetail(@Path("id")int id,@Query("api_key")String api_key );
    }

    //测试连接状态
    public static void getSearchResult(String api_key,String language,String movieName,boolean include_adult,int page,Observer<TmdbSearchResult> observer){
        setSubscribe(service.searchResult(api_key,language,movieName,include_adult,page),observer);
    }

    //获取单个用户信息
    public static void getMovieDetail(int id,String api_key,Observer<TmdbMovieDetail> observer){
        setSubscribe(service.movieDetail(id,api_key),observer);
    }


    //以后讨论一下这里定义为静态的是否可行.....这里就暂时不处理了.....
    public static <T> void setSubscribe(Observable<T> observable, Observer<T> observer) {
        observable.subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())//子线程访问网络
                .observeOn(AndroidSchedulers.mainThread())//回调到主线程
                .subscribe(observer);
    }

}
