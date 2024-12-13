package com.hi.live.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.adaptor.ScreenNewSlidePagerAdapter;
import com.hi.live.databinding.ActivityMainBinding;
import com.hi.live.models.UserRoot;
import com.hi.live.oflineModels.NotificationIntent;
import com.hi.live.popus.PrivacyPopup_g;
import com.hi.live.retrofit.ApiCalling_a;
import com.hi.live.retrofit.Const_a;
import com.hi.live.retrofit.RetrofitBuilder_a;
import com.hi.live.socket.MySocketManager;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivityG_a extends BaseActivity_a {
    private static final String TAG = "mainact";
    private static final int PERMISSION_REQ_ID = 22;
    private static final int REQ_ID = 1;
    boolean exit = false;
    private long backPressedTime;
    private static final String[] REQUESTED_PERMISSIONS = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,

    };
    public static String[] storge_permissions_33 = {Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.POST_NOTIFICATIONS};

    public static boolean isHostLive = false;
    ActivityMainBinding binding;
    SessionManager__a sessionManager;

    private String userId;
    private ScreenNewSlidePagerAdapter screenSlidePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isHostLive = false;
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        sessionManager = new SessionManager__a(this);
        userId = sessionManager.getUser().getId();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (checkSelfPermission(storge_permissions_33[0], REQ_ID) && checkSelfPermission(storge_permissions_33[1], REQ_ID) && checkSelfPermission(storge_permissions_33[2], REQ_ID) && checkSelfPermission(storge_permissions_33[3], REQ_ID) && checkSelfPermission(storge_permissions_33[4], REQ_ID) && checkSelfPermission(storge_permissions_33[5], REQ_ID)) {
                init();
            } else {
                ActivityCompat.requestPermissions(MainActivityG_a.this, storge_permissions_33, REQ_ID);
            }

        } else {
            Log.e(TAG, "onCreate: >>>>>>>>>>>>>  22 ");
            if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) && checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) && checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID) && checkSelfPermission(REQUESTED_PERMISSIONS[3], PERMISSION_REQ_ID)) {
                init();
            } else {

                ActivityCompat.requestPermissions(MainActivityG_a.this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);

            }

        }

        initMain();
        if (!MySocketManager.getInstance().globalConnecting || !MySocketManager.getInstance().globalConnected) {
            getApp().initGlobalSocket();
        }


//        new SimplePopup_g(this);

    }

    private void init() {
        if (!sessionManager.getBooleanValue(Const_a.POLICY_ACCEPTED)) {
            new PrivacyPopup_g(this, new PrivacyPopup_g.OnSubmitClickListnear() {
                @Override
                public void onAccept() {
                    sessionManager.saveBooleanValue(Const_a.POLICY_ACCEPTED, true);

                }

                @Override
                public void onDeny() {
                    finishAffinity();
                }
            });
        } else {
            initMain();
        }
    }

    private void initMain() {

        getIntentData();
        initView();
    }


    @Override
    protected void onResume() {
        super.onResume();
        MainActivityG_a.isHostLive = false;
    }


    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {


            String objStr = intent.getStringExtra(Const_a.notificationIntent);
            if (objStr != null && !objStr.equals("")) {
                NotificationIntent notificationIntent = new Gson().fromJson(objStr, NotificationIntent.class);
                if (notificationIntent.getType() == NotificationIntent.CHAT) {

                    startActivity(new Intent(this, ChatListActivityGOriginal_a.class).putExtra("hostid", notificationIntent.getHostid()).putExtra("name", notificationIntent.getName()).putExtra("image", notificationIntent.getImage()));

                    setUpFragment(2);

                } else if (notificationIntent.getType() == NotificationIntent.LIVE) {
                    startActivity(new Intent(this, WatchLiveActivity_a.class).putExtra("model", new Gson().toJson(notificationIntent.getThumb())));
                }
            }

        }
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 3000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Press back again to leave the app.", Toast.LENGTH_LONG).show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    private void setUpFragment(int position) {
        binding.viewpagerMain.setCurrentItem(position);
    }

    private void initView() {
        screenSlidePagerAdapter = new ScreenNewSlidePagerAdapter(MainActivityG_a.this);
        binding.viewpagerMain.setAdapter(screenSlidePagerAdapter);
        binding.viewpagerMain.setUserInputEnabled(false);

        binding.bottomNavigationView.setSelectedItemId(R.id.navHome);
        binding.bottomNavigationView.setItemIconTintList(null);
        binding.bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.themepurple));

        setUpFragment(0);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navHome) {
                setUpFragment(0);
                return true;
            } else if (itemId == R.id.navMatch) {
                setUpFragment(1);
                return true;
            }
            else if (itemId == R.id.navMessage) {
                setUpFragment(2);
                return true;
            }
            else if (itemId == R.id.navAdd) {
                setUpFragment(3);
                return true;
            }
            else if (itemId == R.id.navProfile) {
                setUpFragment(4);
                return true;
            }
            return false;
        });
        ApiCalling_a apiCallingA = new ApiCalling_a(this);
        apiCallingA.updateUserFollowerCount();
    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }

        return true;
    }

    private void updateRate(String rate) {
        RequestBody coin = RequestBody.create(MediaType.parse("text/plain"), rate);
        RequestBody userid = RequestBody.create(MediaType.parse("text/plain"),userId);
        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("user_id", userid);
        map.put("rate", coin);

        Call<UserRoot> call = RetrofitBuilder_a.create().updateUser(Const_a.DEVKEY, map);
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && response.body().getUser() != null) {
                        Toast.makeText(MainActivityG_a.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        sessionManager.saveUser(response.body().getUser());

                    } else {
                        Toast.makeText(MainActivityG_a.this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {
//ll
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG, "onRequestPermissionsResult: " + requestCode);
        if (requestCode == PERMISSION_REQ_ID) {
//            Log.e(TAG, "onRequestPermissionsResult: " + PackageManager.PERMISSION_GRANTED + "  " + grantResults[0] + " " + grantResults[1]);
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED || grantResults[2] != PackageManager.PERMISSION_GRANTED || grantResults[3] != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "onRequestPermissionsResult: 11 ");

                showLongToast("Need permissions " + Manifest.permission.RECORD_AUDIO + "/" + Manifest.permission.CAMERA + "/" + Manifest.permission.WRITE_EXTERNAL_STORAGE);
                finish();
                return;
            }

            // Here we continue only if all permissions are granted.
            // The permissions can also be granted in the system settings manually.
            init();
        } else {
            if(grantResults.length != 0) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED || grantResults[2] != PackageManager.PERMISSION_GRANTED || grantResults[3] != PackageManager.PERMISSION_GRANTED || grantResults[4] != PackageManager.PERMISSION_GRANTED || grantResults[5] != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "onRequestPermissionsResult: 22 ");

                    showLongToast("Need permissions " + Manifest.permission.RECORD_AUDIO + "/" + Manifest.permission.CAMERA + "/" + Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    finish();
                    return;
                }
            }

            // Here we continue only if all permissions are granted.
            // The permissions can also be granted in the system settings manually.
            init();
        }
    }


    private void showLongToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();

    }


    public void onClickKhajano(View view) {
        startActivity(new Intent(this, MyWalletActivity_a.class));
    }

}