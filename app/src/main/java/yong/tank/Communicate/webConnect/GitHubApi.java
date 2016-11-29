package yong.tank.Communicate.webConnect;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by hasee on 2016/11/28.
 */

public interface GitHubApi {

    @GET("repos/{owner}/{repo}/contributors")
    Call<ResponseBody> contributorsBySimpleGetCall(@Path("owner") String owner, @Path("repo") String repo);
}
