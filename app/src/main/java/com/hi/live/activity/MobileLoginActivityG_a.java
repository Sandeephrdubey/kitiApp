package com.hi.live.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.JsonObject;
import com.hi.live.LivexUtils_a;
import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.databinding.ActivityMobileLoginBinding;
import com.hi.live.databinding.LoginPageBinding;
import com.hi.live.models.User;
import com.hi.live.models.UserRoot;
import com.hi.live.retrofit.ApiCalling_a;
import com.hi.live.retrofit.Const_a;
import com.hi.live.retrofit.RetrofitBuilder_a;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MobileLoginActivityG_a extends BaseActivity_a {
    private static final String TAG = "mobileact";
    private static int STATUS = 1;
    ActivityMobileLoginBinding binding;
    //LoginPageBinding binding;
    String Country = "";
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mobileNumber = "";
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;
    private SessionManager__a sessionManager;
    private User user;
    private String gender = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mobile_login);
       // binding = DataBindingUtil.setContentView(this, R.layout.login_page);
        sessionManager = new SessionManager__a(this);
        mAuth = FirebaseAuth.getInstance();
        setUI("mobile");

        Intent intent = getIntent();
        gender = intent.getStringExtra("gender");
        if (gender != null) {
            ApiCalling_a apiCalling = new ApiCalling_a(this);
            apiCalling.getCountryByIp(LivexUtils_a.getIPAddress(true), new ApiCalling_a.IpResponseListnear() {
                @Override
                public void responseSuccess(String country) {

                    Country = country;

                }

                @Override
                public void responseFail() {
                    Country = sessionManager.getStringValue(Const_a.Country);

                }
            });
        } else {
            Toast.makeText(this, "Select Gender first", Toast.LENGTH_SHORT).show();
            finish();
        }


    }

    private void mCallbacks() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                binding.pd.setVisibility(View.GONE);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.d(TAG, "onVerificationFailed", e);
                Toast.makeText(MobileLoginActivityG_a.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                // The SMS quota for the project has been exceeded
                // ...
                binding.pd.setVisibility(View.GONE);
                if (e instanceof FirebaseAuthInvalidCredentialsException || e instanceof FirebaseTooManyRequestsException) {
                    // Invalid request
                    // ...
                }
                Toast.makeText(MobileLoginActivityG_a.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);
                Toast.makeText(MobileLoginActivityG_a.this, "OTP sended", Toast.LENGTH_SHORT).show();
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                setUI("OTP");
                binding.tvresend.setVisibility(View.GONE);

                new CountDownTimer(60000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        binding.tvTimer.setText(String.valueOf(millisUntilFinished / 1000) + " Second");
                        //here you can have your logic to set text to edittext
                    }

                    public void onFinish() {
                        binding.tvTimer.setVisibility(View.GONE);
                        binding.tvresend.setVisibility(View.VISIBLE);
                    }

                }.start();
                // ...
            }
        };

    }

    private void setUI(String status) {
        if (status.equals("mobile")) {
            binding.tvresend.setVisibility(View.GONE);
            binding.tvTimer.setVisibility(View.GONE);
            binding.otplyt.setVisibility(View.GONE);
            binding.loginbtn.setText("Get Otp");

            STATUS = 1;
        } else if (status.equals("OTP")) {
            binding.tvresend.setVisibility(View.GONE);
            binding.tvTimer.setVisibility(View.VISIBLE);
            binding.otplyt.setVisibility(View.VISIBLE);
            binding.etNumber.setEnabled(false);
            binding.loginbtn.setText("Verify");

            STATUS = 2;
        } else if (status.equals("verified")) {
            binding.tvresend.setVisibility(View.GONE);
            binding.tvTimer.setVisibility(View.GONE);
            binding.otplyt.setVisibility(View.GONE);
            binding.lyt2.setVisibility(View.VISIBLE);
            binding.etNumber.setEnabled(false);
            binding.loginbtn.setText("Submit");
            STATUS = 3;
        }
    }


    public void onClickBack(View view) {
        onBackPressed();
    }

    public void onClickResendOTP(View view) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobileNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                mResendToken);
    }

    public void submitButtonClick(View view) {
        if (STATUS == 1) {
            binding.pd.setVisibility(View.VISIBLE);
            //sendOtpRequest();
            startActivity(new Intent(MobileLoginActivityG_a.this, MainActivityG_a.class));

        } else if (STATUS == 2) {
            String otp = binding.etOtp.getText().toString();
            if (otp.equals("")) {
                Toast.makeText(this, "Enter OTP First", Toast.LENGTH_SHORT).show();
                return;
            }
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
            // [END verify_with_code]
            binding.pd.setVisibility(View.VISIBLE);
          //  signInWithPhoneAuthCredential(credential);

        } else if (STATUS == 3) {
            //sendDataToServer();

            Toast.makeText(this, "SUCCESS", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendDataToServer() {
        binding.pd.setVisibility(View.VISIBLE);
        String name = binding.etName.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }
        binding.pd.setVisibility(View.VISIBLE);

        String imageUrl;
        if (gender.equals("MALE")) {
            imageUrl = Const_a.IMAGE_MALE;
        } else {
            imageUrl = Const_a.IMAGE_FEMALE;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("mobileNo", mobileNumber);
        jsonObject.addProperty("identity", mobileNumber);
        jsonObject.addProperty("type", "mobile");
        jsonObject.addProperty("image", imageUrl);
        jsonObject.addProperty("username", name);
        jsonObject.addProperty("country", Country);
        jsonObject.addProperty("IPAddress", LivexUtils_a.getIPAddress(true));
        jsonObject.addProperty("fcmtoken", sessionManager.getStringValue(Const_a.NOTIFICATION_TOKEN));
        jsonObject.addProperty("gender", gender);
        jsonObject.addProperty("IPAddress", LivexUtils_a.getIPAddress(true));

        Call<UserRoot> call = RetrofitBuilder_a.create().signUpUser(Const_a.DEVKEY, jsonObject);
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200 && response.body().isStatus() && response.body().getUser() != null) {
                    user = response.body().getUser();
                    sessionManager.saveUser(user);

                    sessionManager.saveBooleanValue(Const_a.ISLOGIN, true);

                    startActivity(new Intent(MobileLoginActivityG_a.this, MainActivityG_a.class));

                }
                binding.pd.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {
                Log.d("TAGlogin", "onFailure: " + t.getMessage());
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        binding.pd.setVisibility(View.VISIBLE);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");

                        FirebaseUser user = task.getResult().getUser();
                        Log.d(TAG, "onComplete: " + user.getPhoneNumber() + "  yess  " + user.getUid());

                        setUI("verified");

                        binding.pd.setVisibility(View.GONE);
                        // ...
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(MobileLoginActivityG_a.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        binding.pd.setVisibility(View.GONE);
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                        }
                    }
                });
    }

    private void sendOtpRequest() {
        String m = binding.etNumber.getText().toString();
        String countryCode = binding.etCountry.getText().toString();


        Log.d(TAG, "onClickGetOTP: " + mobileNumber);
        if (m.equals("")) {
            binding.pd.setVisibility(View.GONE);
            binding.etNumber.setError("Enter Mobile First");
            return;
        }
        if (countryCode.equals("")) {
            Toast.makeText(this, "Enter Country Code", Toast.LENGTH_SHORT).show();
            return;
        }
        if (m.length() != 10) {
            binding.pd.setVisibility(View.GONE);
            binding.etNumber.setError("Mobile Number should be 10 digit");
            return;
        }

        mobileNumber = countryCode + m;

        mCallbacks();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobileNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);


    }
}