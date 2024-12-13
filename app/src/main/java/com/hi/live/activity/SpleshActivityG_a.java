package com.hi.live.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.models.AdvertisementRoot;
import com.hi.live.models.BannerRoot;
import com.hi.live.models.IpAddressDataRoot;
import com.hi.live.models.SettingsRoot;
import com.hi.live.models.Thumb;
import com.hi.live.models.UserRoot;
import com.hi.live.oflineModels.Filters.FilterUtils;
import com.hi.live.oflineModels.NotificationIntent;
import com.hi.live.retrofit.Const_a;
import com.hi.live.retrofit.RetrofitBuilder_a;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SpleshActivityG_a extends BaseActivity_a {

    private static final String TAG = "SpleshActivityG_a";
    NotificationIntent notificationIntent;
    private SessionManager__a sessionManager;
    private boolean isnotification = false;
    private AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivityG_a.isHostLive = false;

//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
      /*  MyApp_g.getInstance().setIsNightModeEnabled(true);
        if (MyApp_g.getInstance().isNightModeEnabled()) {
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }*/

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splesh);
        sessionManager = new SessionManager__a(this);
        Log.i("oooo","xxxxxxxxxx");
        FilterUtils.setFilters();
        FilterUtils.setGifs();


        String locale = this.getResources().getConfiguration().locale.getCountry();
        Locale english = Locale.ENGLISH;
        Locale loc = new Locale(english.getLanguage(), locale);
        Log.d("TAG", "onCreate: sss " + loc.getDisplayCountry(english));
        sessionManager.saveStringValue(Const_a.Country, loc.getDisplayCountry(loc));
        FirebaseMessaging.getInstance().subscribeToTopic("WAWE").addOnCompleteListener(task -> Log.d("TAG", "onCreate: init msg"));
        Intent intent = getIntent();
        if (intent != null) {
            Bundle b = intent.getExtras();
            if (b != null) {
                Set<String> keys = b.keySet();
                for (String ignored : keys) {
                    String type = String.valueOf(getIntent().getExtras().get("notificationType"));

                    if (!type.equals("") && !type.equals("null")) {

                        switch (type) {
                            case "chat":

                                String hostid = String.valueOf(getIntent().getExtras().get("hostId"));
                                String name = String.valueOf(getIntent().getExtras().get("name"));
                                String image = String.valueOf(getIntent().getExtras().get("image"));
                                notificationIntent = new NotificationIntent();
                                notificationIntent.setType(NotificationIntent.CHAT);
                                notificationIntent.setHostid(hostid);
                                notificationIntent.setImage(image);
                                notificationIntent.setName(name);
                                isnotification = true;
                                Log.d("notificationData", "onCreate: obj  " + notificationIntent.toString());
                                break;

                            case "android":
                                String image1 = String.valueOf(getIntent().getExtras().get("image"));
                                String hostid1 = String.valueOf(getIntent().getExtras().get("host_id"));
                                String name1 = String.valueOf(getIntent().getExtras().get("name"));
                                String cid = String.valueOf(getIntent().getExtras().get("country_id"));
                                String type1 = String.valueOf(getIntent().getExtras().get("type"));
                                String coin = String.valueOf(getIntent().getExtras().get("coin"));
                                String token = String.valueOf(getIntent().getExtras().get("token"));
                                String channel = String.valueOf(getIntent().getExtras().get("channel"));
                                String view = String.valueOf(getIntent().getExtras().get("view"));


                                Thumb thumb = new Thumb();
                                thumb.setImage(image1);
                                thumb.setHostId(hostid1);
                                thumb.setName(name1);
                                thumb.setCountryId(cid);
                                thumb.setType(type1);
                                thumb.setCoin(Integer.parseInt(coin));
                                thumb.setToken(token);
                                thumb.setChannel(channel);
                                thumb.setView(Integer.parseInt(view));

                                notificationIntent = new NotificationIntent();
                                notificationIntent.setType(NotificationIntent.LIVE);
                                notificationIntent.setThumb(thumb);
                                isnotification = true;
                                Log.d("notificationData", "onCreate: obj  " + notificationIntent.toString());
                                break;
                            default:
                                isnotification = false;
                                break;

                        }


                    } else {
                        isnotification = false;
                    }

                }


            } else {
                Log.w("notificationData", "onCreate: BUNDLE is null");
            }
        } else {
            Log.w("notificationData", "onCreate: INTENT is null");
        }


        FirebaseApp.initializeApp(this);

        getCountryByIp();
        getAdvertisement();
        getBanner();
        getted2();
    }

    private void getBanner() {
        Call<BannerRoot> call = RetrofitBuilder_a.create().getBanner(Const_a.DEVKEY);
        call.enqueue(new Callback<BannerRoot>() {
            @Override
            public void onResponse(Call<BannerRoot> call, Response<BannerRoot> response) {
                if (response.code() == 200 && response.body().isStatus() && response.body().getBanner() != null) {


                    if (response.body().getBanner() != null && !response.body().getBanner().isEmpty()) {
                        sessionManager.saveBooleanValue(Const_a.DownloadBanner, true);
                    } else {
                        sessionManager.saveBooleanValue(Const_a.DownloadBanner, false);
                    }
                    sessionManager.saveBanner(response.body());
                } else {
                    sessionManager.saveBooleanValue(Const_a.DownloadBanner, false);
                    Log.d(TAG, "onResponse:get baner false");
                }
            }

            @Override
            public void onFailure(Call<BannerRoot> call, Throwable t) {

            }
        });
    }

    public void getCountryByIp() {
        Call<IpAddressDataRoot> call = RetrofitBuilder_a.getCountryByIp().getCountryByIp();
        call.enqueue(new Callback<IpAddressDataRoot>() {
            @Override
            public void onResponse(Call<IpAddressDataRoot> call, Response<IpAddressDataRoot> response) {
                if (response.code() == 200 && response.body() != null) {
                    if (response.body().getCountry() != null) {
                        sessionManager.saveStringValue(Const_a.Country_CODE, response.body().getCountryCode());
                        sessionManager.saveStringValue(Const_a.Country, response.body().getCountry());
                        Log.d("TAG", "onResponse: " + response.body().getCountry());
                    } else {
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<IpAddressDataRoot> call, Throwable t) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        Date newDate = calendar.getTime();

        Log.d("TAG", "getDate: ne  " + newDate);

        DateTimeFormatter dtfInput = DateTimeFormatter.ofPattern("E MMM d H:m:s O u", Locale.ENGLISH);
        OffsetDateTime odt = OffsetDateTime.parse(String.valueOf(newDate), dtfInput);

        // Default string i.e. OffsetDateTime#toString
        System.out.println(odt);

        String s = odt.toString().split("T")[0];
        Log.d("TAG", "getDate: dsd  " + s);
    }


    private void getAdvertisement() {
        Call<AdvertisementRoot> call = RetrofitBuilder_a.create().getAdvertisement(Const_a.DEVKEY);
        call.enqueue(new Callback<AdvertisementRoot>() {
            @Override
            public void onResponse(Call<AdvertisementRoot> call, Response<AdvertisementRoot> response) {
                if (response.code() == 200 && response.body().isStatus()) {
                    sessionManager.saveAds(response.body());

                }
            }

            @Override
            public void onFailure(Call<AdvertisementRoot> call, Throwable t) {
                Toast.makeText(SpleshActivityG_a.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getted2() {
        Call<SettingsRoot> call = RetrofitBuilder_a.create().getSettings(Const_a.DEVKEY);
        call.enqueue(new Callback<SettingsRoot>() {
            @Override
            public void onResponse(Call<SettingsRoot> call, Response<SettingsRoot> response) {
                if (response.code() == 200 && response.body().isStatus() && response.body().getData() != null) {
                    sessionManager.saveSetting(response.body().getData());
                    initMain();
                }
            }

            @Override
            public void onFailure(Call<SettingsRoot> call, Throwable t) {
                Log.d(TAG, "onFailure: SettingsRoot   === " + t.getMessage());
                Toast.makeText(SpleshActivityG_a.this, "SettingsRoot" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initMain() {
        if (sessionManager.getBooleanValue(Const_a.ISLOGIN)) {
            Call<UserRoot> call = RetrofitBuilder_a.create().getProfile(Const_a.DEVKEY, sessionManager.getUser().getId());
            call.enqueue(new Callback<UserRoot>() {
                @Override
                public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                    if (response.code() == 200) {
                        Log.d("Response", "Response Code: " + response.code());


                        if (response.body().isStatus()) {
                            if (response.body().getUser() != null) {
                                if (response.body().getUser().isBlock()) {
                                    showBlockMessage("You are blocked by Admin");
                                } else {
                                    sessionManager.saveUser(response.body().getUser());

                                    if (isnotification && notificationIntent != null) {
                                        startActivity(new Intent(SpleshActivityG_a.this, MainActivityG_a.class).putExtra(Const_a.notificationIntent, new Gson().toJson(notificationIntent)));
                                    } else {
                                        startActivity(new Intent(SpleshActivityG_a.this, MainActivityG_a.class));
                                    }
                                }
                            }
                        } else {
                            startActivity(new Intent(SpleshActivityG_a.this, LoginActivityG_a.class));
                            finish();
                        }

                    }
                }

                @Override
                public void onFailure(Call<UserRoot> call, Throwable t) {
//ll
                }
            });


        } else {

            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        Log.d("TAG", "onComplete: fcm tkn== " + token);
                        sessionManager.saveStringValue(Const_a.NOTIFICATION_TOKEN, token);
                        new Handler(Looper.myLooper()).postDelayed(() -> startActivity(new Intent(SpleshActivityG_a.this, LoginActivityG_a.class).putExtra("website", sessionManager.getSetting().getPolicyLink()).putExtra("title", "About Us")), 1000);

                    });
        }
    }

    private void showBlockMessage(String s) {
        builder = new AlertDialog.Builder(this);
        builder.setMessage(s);
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            finishAffinity();
        });

        builder.show();
    }

}