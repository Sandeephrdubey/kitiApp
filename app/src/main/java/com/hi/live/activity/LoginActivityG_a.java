package com.hi.live.activity;

import static android.provider.MediaStore.MediaColumns.DATA;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonObject;
import com.hi.live.LivexUtils_a;
import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.bottomsheet.BottomSheetGender_g;
import com.hi.live.bottomsheet.BottumsheetWebsite;
import com.hi.live.databinding.ActivityLoginBinding;
import com.hi.live.databinding.BottomSheetUpdateprofileBinding;
import com.hi.live.models.RestResponse;
import com.hi.live.models.User;
import com.hi.live.models.UserRoot;
import com.hi.live.retrofit.ApiCalling_a;
import com.hi.live.retrofit.Const_a;
import com.hi.live.retrofit.RetrofitBuilder_a;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivityG_a extends BaseActivity_a {

    private static final int PERMISSION_REQUEST_CODE = 101;
    private static final int GALLERY_CODE = 1001;
    private static final int RC_SIGN_IN = 100;
    private static final String STR_IMAGE = "image";
    private static final String STR_USERNAME = "username";
    private static final String STR_TEXT = "text/plain";
    private static final String TAG = "LoginActivityG_a";
    SessionManager__a sessionManager;
    FirebaseAuth mAuth;
    BottomSheetUpdateprofileBinding sheetDilogBinding;
    Call<RestResponse> userNameCall;
    Uri selectedImage;
    ActivityLoginBinding binding;
    String gender = "";
    private GoogleSignInClient mGoogleSignInClient;
    private BottomSheetDialog bottomSheetDialog;
    private String picturePath;
    private boolean userNameExist = false;
    private boolean emptyname = false;
    private boolean emptyusername = false;
    private User user;
    private CallbackManager mCallbackManager;

    BottumsheetWebsite bottumsheetWebsite;
    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        bottumsheetWebsite = new BottumsheetWebsite(this);
        sessionManager = new SessionManager__a(this);

//        binding.privacy.setOnClickListener(view ->
//                startActivity(new Intent(LoginActivityG_a.this, WebActivity_a.class)
//                        .putExtra("website", sessionManager.getSetting().getPolicyLink())
//                        .putExtra("title", "Privacy Policy"))
//        );

        binding.privacy.setOnClickListener(view ->
                bottumsheetWebsite.showWebSheet(sessionManager.getSetting().getPolicyLink())
        );

        MainActivityG_a.isHostLive = false;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();

        bottomSheetDialog = new BottomSheetDialog(LoginActivityG_a.this, R.style.CustomBottomSheetDialogTheme);
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        bottomSheetDialog.setOnShowListener(dialog -> {
//            BottomSheetDialog d = (BottomSheetDialog) dialog;
//            FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
//            BottomSheetBehavior.from(bottomSheet)
//                    .setState(BottomSheetBehavior.STATE_EXPANDED);
//        });
        sheetDilogBinding = DataBindingUtil.inflate(LayoutInflater.from(LoginActivityG_a.this), R.layout.bottom_sheet_updateprofile, null, false);


    }

    private void openGenderSheet(LoginType type) {
        new BottomSheetGender_g(type, this, new BottomSheetGender_g.OnGenderSelectListner() {
            @Override
            public void onSelect(String g, String name) {

                if (g.isEmpty()) {
                    Toast.makeText(LoginActivityG_a.this, "Select Gender First", Toast.LENGTH_SHORT).show();
                    return;
                }
                gender = g;
                attemptLogin(type, name);
            }
        });
    }

    private void attemptLogin(LoginType type, String name) {
        if (type == LoginType.google) {
            binding.animationView.setVisibility(View.GONE);
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else if (type == LoginType.facebook) {
            LoginButton loginButton = binding.loginButton;
            loginButton.setPermissions("email", "public_profile");
            loginButton.setReadPermissions("email", "public_profile", "user_friends");

            loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d("TAG", "facebook:onSuccess:" + loginResult);
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

                private void handleFacebookAccessToken(AccessToken token) {
                    Log.d("TAG", "handleFacebookAccessToken:" + token);

                    AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
                    mAuth.signInWithCredential(credential)
                            .addOnCompleteListener(LoginActivityG_a.this, task -> {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("TAG", "signInWithCredential:success");
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    String imageUrl;
                                    if (gender.equals("MALE")) {
                                        imageUrl = Const_a.IMAGE_MALE;
                                    } else {
                                        imageUrl = Const_a.IMAGE_FEMALE;
                                    }
                                    String[] username = user.getEmail().split("@");
                                    JsonObject jsonObject = new JsonObject();
                                    jsonObject.addProperty("name", user.getDisplayName());
                                    jsonObject.addProperty("identity", user.getEmail());
                                    jsonObject.addProperty(STR_IMAGE, user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : imageUrl);
                                    jsonObject.addProperty(STR_USERNAME, username[0]);
                                    jsonObject.addProperty("type", "fb");
                                    signupUser(jsonObject);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("TAG", "signInWithCredential:failure", task.getException());
                                    Toast.makeText(LoginActivityG_a.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }


                            });
                }

                @Override
                public void onCancel() {
                    Log.d("TAG", "facebook:onCancel");
                    // ...
                }

                @Override
                public void onError(FacebookException error) {
                    Log.d("TAG", "facebook:onError" + error.toString(), error);
                    // ...
                }
            });
            loginButton.performClick();

        } else if (type == LoginType.mobile) {
           // startActivity(new Intent(this, MobileLoginActivityG_a.class).putExtra("gender", gender));
            startActivity(new Intent(this, NewMobileLoginActivityG_a.class));
        } else {
            binding.lytquick.setEnabled(false);
            binding.animationView.setVisibility(View.VISIBLE);
            String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);


            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", name);
            jsonObject.addProperty("identity", androidId);
            Log.d("TAG", "attemptLogin: g  " + gender);
            if (gender.equals("MALE")) {
                jsonObject.addProperty(STR_IMAGE, Const_a.IMAGE_MALE);
            } else {
                jsonObject.addProperty(STR_IMAGE, Const_a.IMAGE_FEMALE);
            }

            jsonObject.addProperty(STR_USERNAME, androidId);
            jsonObject.addProperty("type", "quick");
            jsonObject.addProperty("gender", gender);
            jsonObject.addProperty("country", sessionManager.getStringValue(Const_a.Country));
            jsonObject.addProperty("fcmtoken", sessionManager.getStringValue(Const_a.NOTIFICATION_TOKEN));
            jsonObject.addProperty("IPAddress", LivexUtils_a.getIPAddress(true));

            Call<UserRoot> call = RetrofitBuilder_a.create().signUpUser(Const_a.DEVKEY, jsonObject);
            call.enqueue(new Callback<UserRoot>() {
                @Override
                public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                    if (response.code() == 200 && response.body().isStatus() && response.body().getUser() != null) {
                        user = response.body().getUser();
                        sessionManager.saveUser(user);
                        sessionManager.saveBooleanValue(Const_a.Is_Guest_Login, true);
                        sessionManager.saveBooleanValue(Const_a.ISLOGIN, true);
                        binding.lytquick.setEnabled(true);
                        startActivity(new Intent(LoginActivityG_a.this, MainActivityG_a.class));
                    }
                    binding.animationView.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<UserRoot> call, Throwable t) {
                    Log.d("TAGlogin", "onFailure: " + t.getMessage());
                }
            });

        }
    }

    public void onClickWithGoogle(View view) {
        openGenderSheet(LoginType.google);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            binding.animationView.setVisibility(View.VISIBLE);
//            binding.lytgoogle.setEnabled(false);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                if (account != null) {
                    Log.d("TAG", "firebaseAuthWithGoogle:" + account.getDisplayName());

                    String imageUrl;
                    if (gender.equals("MALE")) {
                        imageUrl = Const_a.IMAGE_MALE;
                    } else {
                        imageUrl = Const_a.IMAGE_FEMALE;
                    }
                    String[] username = account.getEmail().split("@");
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("name", account.getDisplayName());
                    jsonObject.addProperty("identity", account.getEmail());
                    jsonObject.addProperty(STR_IMAGE, account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : imageUrl);
                    jsonObject.addProperty(STR_USERNAME, username[0]);
                    jsonObject.addProperty("type", "google");
                    signupUser(jsonObject);

                }

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
                // ...
            }
        }

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && null != data) {

            selectedImage = data.getData();

            Glide.with(this)
                    .load(selectedImage)
                    .circleCrop()
                    .into(sheetDilogBinding.imgUser);
            String[] filePathColumn = {DATA};

            Cursor cursor = LoginActivityG_a.this.getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();


        }

    }

    private void sendDataToServer(JsonObject jsonObject, String country) {
        binding.animationView.setVisibility(View.VISIBLE);


        jsonObject.addProperty("country", country);
        jsonObject.addProperty("IPAddress", LivexUtils_a.getIPAddress(true));
        jsonObject.addProperty("fcmtoken", sessionManager.getStringValue(Const_a.NOTIFICATION_TOKEN));
        jsonObject.addProperty("gender", gender);
        jsonObject.addProperty("IPAddress", LivexUtils_a.getIPAddress(true));


        Call<UserRoot> call = RetrofitBuilder_a.create().signUpUser(Const_a.DEVKEY, jsonObject);
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && response.body().getUser() != null) {
                        user = response.body().getUser();
                        if (user.isIsHost()) {
                            showBlockMessage("You are host now");
                            return;
                        } else if (user.isBlock()) {
                            showBlockMessage("You are blocked by Admin");
                            return;
                        }


                        sessionManager.saveUser(user);
                        sessionManager.saveBooleanValue(Const_a.ISLOGIN, true);
                        finish();
                        binding.lytgoogle.setEnabled(true);
                        startActivity(new Intent(LoginActivityG_a.this, MainActivityG_a.class));
                    }
                }
                binding.animationView.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {
                Log.d("TAGlogin", "onFailure: " + t.getMessage());
            }
        });
    }

    private void signupUser(JsonObject jsonObject) {
        ApiCalling_a apiCalling = new ApiCalling_a(this);
        apiCalling.getCountryByIp(LivexUtils_a.getIPAddress(true), new ApiCalling_a.IpResponseListnear() {
            @Override
            public void responseSuccess(String country) {

                sendDataToServer(jsonObject, country);

            }

            @Override
            public void responseFail() {
                sendDataToServer(jsonObject, sessionManager.getStringValue(Const_a.Country));
            }
        });


    }

    public void onClickQuickLogin(View view) {
        openGenderSheet(LoginType.quick);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
        }
    }

    private void showBlockMessage(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(s);
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            finishAffinity();
        });

        builder.show();
    }


    private void choosePhoto() {
        if (checkPermission()) {
            Intent i = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, GALLERY_CODE);
        } else {
            requestPermission();
        }
    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(LoginActivityG_a.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivityG_a.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(LoginActivityG_a.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(LoginActivityG_a.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("value", "Permission Granted, Now you can use local drive .");
                choosePhoto();
            } else {
                Log.e("value", "Permission Denied, You cannot use local drive .");
            }
        }
    }

    public void onClickLoginFacebook(View view) {

        openGenderSheet(LoginType.facebook);
    }

    public void onClickMobile(View view) {
        openGenderSheet(LoginType.mobile);

    }

    public enum LoginType {
        google, facebook, quick, mobile;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ma jay ceh ===== ");
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
}