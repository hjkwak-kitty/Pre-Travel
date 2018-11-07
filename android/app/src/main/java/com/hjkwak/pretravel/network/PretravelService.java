package com.hjkwak.pretravel.network;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * android
 * Class: PretravelService
 * Created by hjkwak on 07/11/2018.
 * <p>
 * Description:
 */
public interface PretravelService {

    //    request Login

    @FormUrlEncoded
    @POST("login.php")
    Call<ResponseBody> requestLogin(@Field("email") String email, @Field("password") String password);
}
