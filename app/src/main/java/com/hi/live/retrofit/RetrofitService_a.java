package com.hi.live.retrofit;

import com.google.gson.JsonObject;
import com.hi.live.models.AdvertisementRoot;
import com.hi.live.models.BannerRoot;
import com.hi.live.models.BecomeVipMemberRoot;
import com.hi.live.models.CallCreateRoot;
import com.hi.live.models.CallHistoryListRoot;
import com.hi.live.models.ChatSendRoot;
import com.hi.live.models.ChatTopicRoot;
import com.hi.live.models.ChatUserListRoot;
import com.hi.live.models.CoinHistoryRoot;
import com.hi.live.models.CommentRootOriginal;
import com.hi.live.models.ComplainRoot;
import com.hi.live.models.CountryDetailRoot;
import com.hi.live.models.CountryRoot;
import com.hi.live.models.DailyTaskRoot;
import com.hi.live.models.EmojiIconRoot;
import com.hi.live.models.EmojicategoryRoot;
import com.hi.live.models.FollowListRoot;
import com.hi.live.models.GirlThumbListRoot;
import com.hi.live.models.GuestUserRoot;
import com.hi.live.models.HostRoot;
import com.hi.live.models.IpAddressDataRoot;
import com.hi.live.models.LevelRoot;
import com.hi.live.models.NotificationRoot;
import com.hi.live.models.OriginalMessageRoot;
import com.hi.live.models.PlanRoot;
import com.hi.live.models.RandomMatchHostRoot;
import com.hi.live.models.RechargeHistoryRoot;
import com.hi.live.models.RestResponse;
import com.hi.live.models.SettingsRoot;
import com.hi.live.models.UserRoot;
import com.hi.live.models.VipPlanRoot;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService_a {

    @GET("json")
    Call<IpAddressDataRoot> getCountryByIp();

    @GET("{countrycode}")
    Call<CountryDetailRoot> getCurrencyCode(@Path("countrycode") String countrycode);

    @POST("/user/signup")
    Call<UserRoot> signUpUser(@Header("key") String devkey, @Body JsonObject object);


    @DELETE("/user/logout")
    Call<RestResponse> logoutUser(@Header("key") String devkey, @Query("user_id") String uid);

    @GET("/user/online")
    Call<RestResponse> onlineUser(@Header("key") String devkey, @Query("user_id") String uid);

    @GET("/user/offline")
    Call<RestResponse> offlineUser(@Header("key") String devkey, @Query("user_id") String uid);


    @Multipart
    @POST("user/edit_profile")
    Call<UserRoot> updateUser(@Header("key") String token,
                              @PartMap Map<String, RequestBody> partMap,
                              @Part MultipartBody.Part requestBody);


    @Multipart
    @POST("user/edit_profile")
    Call<UserRoot> updateUser(@Header("key") String token,
                              @PartMap Map<String, RequestBody> partMap);


    @GET("/user/profile")
    Call<UserRoot> getProfile(@Header("key") String devkey, @Query("user_id") String uid);

    @GET("/country")
    Call<CountryRoot> getCountries(@Header("key") String devkey);


    @GET("/thumblist")
    Call<GirlThumbListRoot> getThumbs(@Header("key") String devkey, @Query("country") String cid, @Query("start") int start, @Query("limit") int limit);


    @GET("/onlinehost")
    Call<GirlThumbListRoot> getOnlineHosts(@Header("key") String devkey, @Query("country") String cid, @Query("start") int start, @Query("limit") int limit);

    @GET("/favouritelist")
    Call<GirlThumbListRoot> getFaviourites(@Header("key") String devkey, @Query("user_id") String uid);

    @GET("/user/profile")
    Call<UserRoot> getUserProfile(@Header("key") String devkey, @Query("user_id") String uid);

    @GET("/host/profile")
    Call<HostRoot> getHostProfile(@Header("key") String devkey, @Query("host_id") String uid);


    @GET("/category")
    Call<EmojicategoryRoot> getCategories(@Header("key") String devkey);

    @GET("/gift/category")
    Call<EmojiIconRoot> getEmojiByCategory(@Header("key") String devkey, @Query("category") String cid);


    @POST("history/transferCoin")
    Call<UserRoot> transferCoin(@Header("key") String devkey, @Body JsonObject object);


    @POST("/livecomment")
    Call<RestResponse> sendCommentToServer(@Header("key") String devkey, @Body JsonObject jsonObject);

    @GET("/livecomment")
    Call<CommentRootOriginal> getOldComments(@Header("key") String devkey, @Header("token") String tkn);


    @POST("/chattopic/add")
    Call<ChatTopicRoot> createChatTopic(@Header("key") String devkey, @Body JsonObject jsonObject);

    @GET("/chattopic")
    Call<ChatUserListRoot> getChatUserList(@Header("key") String devkey, @Query("user_id") String uid, @Query("start") int start, @Query("limit") int limit);

    @GET("/chat/userOldChat")
    Call<OriginalMessageRoot> getOldMessage(@Header("key") String devkey, @Query("user_id") String user_id, @Query("type") String type, @Query("topic") String topic, @Query("start") int start, @Query("limit") int limit);

    @POST("/chat/add")
    Call<ChatSendRoot> sendMessageToBackend(@Header("key") String devkey, @Body JsonObject jsonObject);

    @GET("/chattopic/search")
    Call<ChatUserListRoot> getSearchList(@Header("key") String devkey, @Query("name") String name, @Query("user_id") String user_id, @Query("start") int start, @Query("limit") int limit);


    @GET("/userFollower/follow")
    Call<RestResponse> follow(@Header("key") String devkey, @Query("user_id") String user_id, @Query("host_id") String host_id);

    @GET("/userFollower/unFollow")
    Call<RestResponse> unfollow(@Header("key") String devkey, @Query("user_id") String user_id, @Query("host_id") String host_id);

    @POST("/user/checkFollow")
    Call<RestResponse> checkFollow(@Header("key") String devkey, @Body JsonObject jsonObject);

    @GET("/userFollower/follower")
    Call<FollowListRoot> followrsList(@Header("key") String devkey, @Query("user_id") String user_id, @Query("start") int start, @Query("limit") int limit);

    @GET("/userFollower/following")
    Call<FollowListRoot> followingList(@Header("key") String devkey, @Query("user_id") String user_id, @Query("start") int start, @Query("limit") int limit);

    @GET("/history/getrecharge")
    Call<RechargeHistoryRoot> getRechargeHistory(@Header("key") String devkey, @Query("user_id") String uid, @Query("start") int start, @Query("limit") int limit);


    @GET("/history/coinoutcome")
    Call<CoinHistoryRoot> getcoinoutflowHistory(@Header("key") String devkey, @Query("user_id") String uid, @Query("start") int start, @Query("limit") int limit);

    @GET("/plan")
    Call<PlanRoot> getPlanList(@Header("key") String devkey);


    @GET("host/unlive")
    Call<RestResponse> destoryHost(@Header("key") String devkey, @Query("user_id") String hostid);


    @POST("/history/purchasecoin")
    Call<RestResponse> purchaseCoin(@Header("key") String devkey, @Body JsonObject jsonObject);


    @POST("user/checkdailytask")
    Call<DailyTaskRoot> checkDailyTask(@Header("key") String devkey, @Body JsonObject jsonObject);

    @POST("user/dailytask")
    Call<RestResponse> updateDailyTask(@Header("key") String devkey, @Body JsonObject jsonObject);


    @GET("notification/user")
    Call<NotificationRoot> getNotifications(@Query("user_id") String userId, @Query("start") int start, @Query("limit") int limit);


    @GET("setting")
    Call<SettingsRoot> getSettings(@Header("key") String devkey);


    @POST("report")
    Call<RestResponse> reportThisUser(@Header("key") String devkey, @Body JsonObject jsonObject);


    @GET("/plan")
    Call<PlanRoot> getPlanListByPaymentGateway(@Header("key") String devkey);

    @GET("/VIPplan/paymentgateway")
    Call<VipPlanRoot> getVipPlans(@Header("key") String devkey);

    @POST("/user/addplan")
    Call<BecomeVipMemberRoot> becomeVip(@Header("key") String devkey, @Body JsonObject jsonObject);


    @GET("/advertisement")
    Call<AdvertisementRoot> getAdvertisement(@Query("key") String key);

    @Multipart
    @POST("complain")
    Call<RestResponse> addSupport(@Header("key") String token,
                                  @PartMap Map<String, RequestBody> partMap,
                                  @Part MultipartBody.Part requestBody);

    @GET("/complain/particular/user")
    Call<ComplainRoot> getComplains(@Header("key") String devkey, @Query("user_id") String userId);


    @POST("/hostisonline")
    Call<RestResponse> hostIsOnline(@Header("key") String token, @Query("host_id") String hostId);


    @Multipart
    @POST("request")
    Call<RestResponse> addHostRequeest(@Header("key") String token,
                                       @PartMap Map<String, RequestBody> partMap,
                                       @Part MultipartBody.Part requestBody);


    @GET("host/connect")
    Call<RestResponse> busyHost(@Header("key") String devkey, @Query("user_id") String hostid);

    @GET("host/disconnect")
    Call<RestResponse> freeHost(@Header("key") String devkey, @Query("user_id") String hostid);


    @GET("hostisbusy")
    Call<RestResponse> hostIsBusyOrNot(@Header("key") String devkey, @Query("host_id") String hostid);


    @POST("/callHistory")
    Call<CallCreateRoot> createCall(@Header("key") String token, @Body JsonObject jsonObject);

    @POST("/callHistory/receive")
    Call<UserRoot> callRecive(@Header("key") String token, @Body JsonObject jsonObject);


    @GET("callHistory/user")
    Call<CallHistoryListRoot> getCallHistoryList(@Header("key") String devkey, @Query("user_id") String userId, @Query("start") int start, @Query("limit") int limit);

    @POST("notification/profileVisit")
    Call<RestResponse> profileVisit(@Header("key") String devkey, @Body JsonObject jsonObject);

    @POST("notification/missCall")
    Call<RestResponse> missCall(@Header("key") String devkey, @Body JsonObject jsonObject);

    @GET("user/level")
    Call<LevelRoot> getLevel(@Header("key") String devkey, @Query("user_id") String userId);

    @GET("/banner")
    Call<BannerRoot> getBanner(@Header("key") String devkey);

    @GET("host/random")
    Call<RandomMatchHostRoot> getRandomHost(@Header("key") String devkey);

    @GET("/user/otherUserProfile")
    Call<GuestUserRoot> getGuestUserProfile(@Header("key") String devkey, @Query("type") String type, @Query("user_id") String user_id, @Query("host_id") String host_id);

    @GET("/user/addTrueFollowerCount")
    Call<RestResponse> updateUserFollowerCount(@Header("key") String devkey, @Query("user_id") String user_id);

}
