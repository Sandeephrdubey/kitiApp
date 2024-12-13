package com.hi.live.fregment;

import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.MediaColumns.DATA;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.hi.live.BuildConfig;
import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.activity.ComplainListActivityG_a;
import com.hi.live.activity.FavouriteActivityG_a;
import com.hi.live.activity.FollowListActivityG_a;
import com.hi.live.activity.HistoryActivityG_a;
import com.hi.live.activity.HostRequestActivityG_a;
import com.hi.live.activity.LevelListActivityG_a;
import com.hi.live.activity.MyWalletActivity_a;
import com.hi.live.activity.SpleshActivityG_a;
import com.hi.live.activity.SupportActivityG_a;
import com.hi.live.activity.WebActivity_a;
import com.hi.live.databinding.BottomSheetUpdateprofileBinding;
import com.hi.live.databinding.FragmentProfileFregmentBinding;
import com.hi.live.models.LevelRoot;
import com.hi.live.models.RestResponse;
import com.hi.live.models.User;
import com.hi.live.models.UserRoot;
import com.hi.live.popus.LogoutPopup;
import com.hi.live.retrofit.Const_a;
import com.hi.live.retrofit.RetrofitBuilder_a;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFregment_g extends Fragment {
    private static final int PERMISSION_REQUEST_CODE = 101;
    private static final int GALLERY_CODE = 1001;
    private static final String STR_TITLE = "title";
    private static final String STRT_TXT = "text/plain";
    FragmentProfileFregmentBinding binding;
    SessionManager__a sessionManager;
    BottomSheetUpdateprofileBinding sheetDilogBinding;
    Call<RestResponse> userNameCall;
    String userID;
    User user;
    BottomSheetDialog bottomSheetDialog;
    Uri selectedImage;
    String picturePath;
    boolean userNameExist = false;
    boolean emptyname = false;
    boolean emptyusername = false;
    private static final int PERMISSION_REQ_ID = 22;

    private static final int REQ_ID = 1;
    private static final String[] REQUESTED_PERMISSIONS = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    public static String[] storge_permissions_33 = {Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

    public ProfileFregment_g() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_fregment, container, false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        sessionManager = new SessionManager__a(getActivity());

        binding.layrefresh.setOnRefreshListener(refreshLayout -> {
            binding.shimmer.setVisibility(View.VISIBLE);
            binding.shimmer.startShimmer();
            binding.lytprofile.setVisibility(View.GONE);
            getData();
            binding.layrefresh.finishRefresh(1000);
        });

        if (sessionManager.getBooleanValue(Const_a.ISLOGIN)) {
            userID = sessionManager.getUser().getId();
            binding.shimmer.setVisibility(View.VISIBLE);
            binding.shimmer.startShimmer();
            binding.lytprofile.setVisibility(View.GONE);
            getData();

        }
        getLevelData();

        if (sessionManager.getUser().isIsHost()) {
            binding.lytbecomehost.setVisibility(View.GONE);
            binding.lytfavourite.setVisibility(View.GONE);
        }
        binding.imgback.setOnClickListener(v -> getActivity().onBackPressed());

        binding.lytbecomehost.setOnClickListener(v -> startActivity(new Intent(getActivity(), HostRequestActivityG_a.class)));
        binding.lytwallet.setOnClickListener(v -> startActivity(new Intent(getActivity(), MyWalletActivity_a.class)));
        binding.lythistory.setOnClickListener(v -> startActivity(new Intent(getActivity(), MyWalletActivity_a.class)));
        binding.lytfavourite.setOnClickListener(v -> startActivity(new Intent(getActivity(), FavouriteActivityG_a.class)));
        binding.lytsupport.setOnClickListener(v -> startActivity(new Intent(getActivity(), SupportActivityG_a.class)));
        binding.lytComplains.setOnClickListener(v -> startActivity(new Intent(getActivity(), ComplainListActivityG_a.class)));


        binding.lytaboutus.setOnClickListener(v -> startActivity(new Intent(getActivity(), WebActivity_a.class).putExtra("website", sessionManager.getSetting().getPolicyLink()).putExtra(STR_TITLE, "About Us")));
        binding.lytShare.setOnClickListener(v -> {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType(STRT_TXT);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                String shareMessage = "\nLet me recommend you this application\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch (Exception e) {
                //ll
            }
        });
        binding.lytrate.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)));
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
                e.printStackTrace();
            }
        });
        binding.lytprivacy.setOnClickListener(v -> startActivity(new Intent(getActivity(), WebActivity_a.class).putExtra("website", sessionManager.getSetting().getPolicyLink()).putExtra(STR_TITLE, "Privacy Policy")));
        binding.lytlogout.setOnClickListener(v -> {
            new LogoutPopup(getActivity(), new LogoutPopup.onLogoutPopupListener() {
                @Override
                public void onLogoutClick() {
                    binding.pd.setVisibility(View.VISIBLE);
                    Call<RestResponse> call = RetrofitBuilder_a.create().logoutUser(Const_a.DEVKEY, userID);
                    call.enqueue(new Callback<RestResponse>() {
                        @Override
                        public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                            if (response.code() == 200 && response.body().isStatus()) {
                                Toast.makeText(getActivity(), "Log out Successfully", Toast.LENGTH_SHORT).show();

                                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
//                                LoginManager.getInstance().logOut();
                                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
                                googleSignInClient.signOut();
                                sessionManager.saveBooleanValue(Const_a.POLICY_ACCEPTED, false);
                                FirebaseAuth.getInstance().signOut();
                                sessionManager.saveBooleanValue(Const_a.ISLOGIN, false);
                                sessionManager.saveUser(null);
                                binding.pd.setVisibility(View.GONE);
                                getActivity().finishAffinity();
                                startActivity(new Intent(getActivity(), SpleshActivityG_a.class));
                            }
                        }

                        @Override
                        public void onFailure(Call<RestResponse> call, Throwable t) {
//ll
                        }
                    });

                }
            });
        });
    }

    private void getLevelData() {
        binding.tvLevel.setVisibility(View.GONE);
        Call<LevelRoot> call = RetrofitBuilder_a.create().getLevel(Const_a.DEVKEY, sessionManager.getUser().getId());
        call.enqueue(new Callback<LevelRoot>() {
            @Override
            public void onResponse(Call<LevelRoot> call, Response<LevelRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getLevel().isEmpty()) {
                        binding.tvLevel.setText(response.body().getLevel());
                        binding.tvLevel.setVisibility(View.VISIBLE);
                        binding.tvLevel.setOnClickListener(v -> startActivity(new Intent(getActivity(), LevelListActivityG_a.class).putExtra("data", new Gson().toJson(response.body()))));
                    }
                }
            }

            @Override
            public void onFailure(Call<LevelRoot> call, Throwable t) {

            }
        });
    }

    private void getData() {
        Log.d("checkinggg ", "getData: profile 178");
        Call<UserRoot> call = RetrofitBuilder_a.create().getUserProfile(Const_a.DEVKEY, userID);
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200 && response.body().isStatus() && response.body().getUser() != null) {
                    user = response.body().getUser();
                    sessionManager.saveUser(user);
                    binding.shimmer.setVisibility(View.GONE);
                    binding.lytprofile.setVisibility(View.VISIBLE);
                    if (getActivity() != null) {
                        setData();
                    } else {
                        getData();
                    }

                }
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {
//ll
            }
        });
    }

    private void setData() {
        Glide.with(requireActivity()).load(user.getImage()).circleCrop().addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                binding.profilePhotoLoader.setVisibility(View.GONE);
                return false;
            }
        }).into(binding.imgprofile);
        binding.tvName.setText(user.getName());
        binding.username.setText("ID : " + user.getUniqueId());
        // binding.username.setText("@" + user.getUsername());
        binding.tvfollowing.setText(String.valueOf(user.getFollowingCount()));
        binding.tvfollowrs.setText(String.valueOf(user.getFollowersCount()));
        binding.tvcoin.setText(String.valueOf(user.getCoin()));
        binding.coins.setText(String.valueOf(user.getCoin()));
        binding.tvbio.setText(user.getBio());
        if (user.getBio() != null && !user.getBio().equals("")) {
            binding.tvbio.setVisibility(View.VISIBLE);
        } else {
            binding.tvbio.setVisibility(View.GONE);
        }

        binding.lytfollowrs.setOnClickListener(v -> startActivity(new Intent(getActivity(), FollowListActivityG_a.class).putExtra(STR_TITLE, "Followers")));
        binding.lytfollowing.setOnClickListener(v -> startActivity(new Intent(getActivity(), FollowListActivityG_a.class).putExtra(STR_TITLE, "Following")));
        binding.lytcoin.setOnClickListener(v -> startActivity(new Intent(getActivity(), HistoryActivityG_a.class)));
        binding.lythistory.setOnClickListener(v -> startActivity(new Intent(getActivity(), HistoryActivityG_a.class)));

        binding.editButton.setOnClickListener(v -> openEditSheet());

    }

    private void openEditSheet() {
        bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.CustomBottomSheetDialogTheme);
//        bottomSheetDialog.setOnShowListener(dialog -> {
//            BottomSheetDialog d = (BottomSheetDialog) dialog;
//            FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
//            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
//        });
        sheetDilogBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.bottom_sheet_updateprofile, null, false);
        bottomSheetDialog.setContentView(sheetDilogBinding.getRoot());
        bottomSheetDialog.show();
        sheetDilogBinding.tvskip.setVisibility(View.GONE);
        sheetDilogBinding.etName.setText(user.getName());
        if (user.getBio() != null && !user.getBio().equals("")) {
            sheetDilogBinding.etbio.setText(user.getBio());
        } else {
            sheetDilogBinding.etbio.setText("");
        }

        Glide.with(getActivity()).load(user.getImage()).circleCrop().addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                sheetDilogBinding.profilechar.setText(String.valueOf(user.getName().charAt(0)).toUpperCase());
                return true;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                sheetDilogBinding.imgUser.setImageDrawable(resource);
                return true;
            }
        }).into(sheetDilogBinding.imgUser);

        sheetDilogBinding.btnPencil.setOnClickListener(v -> choosePhoto());


        sheetDilogBinding.tvSubmit.setOnClickListener(v -> updateProfile());
        sheetDilogBinding.btnclose.setOnClickListener(v -> bottomSheetDialog.dismiss());
    }

    private void updateProfile() {
        bottomSheetDialog.dismiss();
        String name = sheetDilogBinding.etName.getText().toString();

        String bio = sheetDilogBinding.etbio.getText().toString();

        if (bio.equals("")) {
            bio = " ";
        }

        if (name.equals("")) {
            emptyname = true;
            sheetDilogBinding.etName.setError("Required!");
            return;
        }

        RequestBody fname = RequestBody.create(MediaType.parse(STRT_TXT), name);
        RequestBody ubio = RequestBody.create(MediaType.parse(STRT_TXT), bio);
        RequestBody userid = RequestBody.create(MediaType.parse(STRT_TXT), userID);

        HashMap<String, RequestBody> map = new HashMap<>();

        MultipartBody.Part body = null;
        if (picturePath != null) {
            File file = new File(picturePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        }

        map.put("user_id", userid);
        map.put("name", fname);
        map.put("bio", ubio);


        Call<UserRoot> call = RetrofitBuilder_a.create().updateUser(Const_a.DEVKEY, map, body);
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && response.body().getUser() != null) {
                        getData();
                    } else {

                    }
                }
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {
//ll
            }
        });
    }


    private void choosePhoto() {
//        if (checkPermission()) {
//            Intent i = new Intent(Intent.ACTION_PICK,
//                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            startActivityForResult(i, GALLERY_CODE);
//        } else {
//            requestPermission();
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.e("TAG", "onCreate: >>>>>>>>>>>>>  11 ");

            if (checkSelfPermission(storge_permissions_33[0], REQ_ID) && checkSelfPermission(storge_permissions_33[1], REQ_ID) && checkSelfPermission(storge_permissions_33[2], REQ_ID) && checkSelfPermission(storge_permissions_33[3], REQ_ID) && checkSelfPermission(storge_permissions_33[4], REQ_ID)) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, GALLERY_CODE);
            } else {
                ActivityCompat.requestPermissions(getActivity(), storge_permissions_33, REQ_ID);
            }

        } else {
            Log.e("TAG", "onCreate: >>>>>>>>>>>>>  22 ");
            if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) && checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) && checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID) && checkSelfPermission(REQUESTED_PERMISSIONS[3], PERMISSION_REQ_ID)) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, GALLERY_CODE);
            } else {

                ActivityCompat.requestPermissions(getActivity(), REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);

            }
        }
    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
            //  ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }
        return true;
    }


    private void showLongToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQ_ID) {

            if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED || grantResults[2] != PackageManager.PERMISSION_GRANTED || grantResults[3] != PackageManager.PERMISSION_GRANTED) {
                Log.e("TAG", "onRequestPermissionsResult: 11 ");

                showLongToast("Need permissions " + Manifest.permission.WRITE_EXTERNAL_STORAGE);
                return;
            }

            choosePhoto();
        } else {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED || grantResults[2] != PackageManager.PERMISSION_GRANTED || grantResults[3] != PackageManager.PERMISSION_GRANTED || grantResults[4] != PackageManager.PERMISSION_GRANTED) {
                Log.e("TAG", "onRequestPermissionsResult: 22 ");

                showLongToast("Need permissions " + "/" + Manifest.permission.WRITE_EXTERNAL_STORAGE);

                return;
            }
            choosePhoto();
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(getActivity(), "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && null != data) {

            selectedImage = data.getData();

            Glide.with(this).load(selectedImage).circleCrop().into(sheetDilogBinding.imgUser);
            String[] filePathColumn = {DATA};

            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();


        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }
}