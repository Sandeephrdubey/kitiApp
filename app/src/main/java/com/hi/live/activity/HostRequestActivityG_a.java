package com.hi.live.activity;

import static android.provider.MediaStore.MediaColumns.DATA;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.databinding.ActivityHostRequestBinding;
import com.hi.live.models.RestResponse;
import com.hi.live.retrofit.Const_a;
import com.hi.live.retrofit.RetrofitBuilder_a;

import java.io.File;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HostRequestActivityG_a extends BaseActivity_a {
    private static final int PERMISSION_REQUEST_CODE = 1001;
    ActivityHostRequestBinding binding;
    SessionManager__a sessionManager;
    private int galleryCode = 123;
    private Uri selectedImage;
    private String picturePath1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_host_request);
        sessionManager = new SessionManager__a(this);


        binding.hostImage.setOnClickListener(v -> {
            permission();
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, galleryCode);
        });

        binding.txtNext.setOnClickListener(v -> sendRequest());


    }

    private void sendRequest() {
        binding.txtNext.setEnabled(false);
        String des = binding.bio.getText().toString();
        String agencyCode = binding.etAgencyCode.getText().toString();


        if (des.isEmpty()) {
            Toast.makeText(this, "Please Enter Bio", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedImage == null || picturePath1 == null || picturePath1.equals("")) {
            Toast.makeText(this, "Please Select Picture", Toast.LENGTH_SHORT).show();
            return;
        }


        RequestBody bio = RequestBody.create(MediaType.parse("text/plain"), des);
        RequestBody agency = RequestBody.create(MediaType.parse("text/plain"), agencyCode);
        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), sessionManager.getUser().getId());
        HashMap<String, RequestBody> map = new HashMap<>();


        MultipartBody.Part body = null;
        if (!picturePath1.isEmpty()) {
            File file = new File(picturePath1);
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);
            body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        }


        map.put("user_id", id);
        map.put("bio", bio);
        map.put("code", agency);
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout_green,
                (ViewGroup) findViewById(R.id.customtoastlyt_green));
        binding.pd.setVisibility(View.VISIBLE);
        Call<RestResponse> call = RetrofitBuilder_a.create().addHostRequeest(Const_a.DEVKEY, map, body);
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus()) {
                        sessionManager.saveBooleanValue(Const_a.IS_FIRST_TIMEHOST, false);

                        if (!isFinishing()) {
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();
                        }
                        finish();

                        /*         new Handler().postDelayed(() -> onBackPressed(), 1500);*/

                    } else {
                        TextView textView = layout.findViewById(R.id.tvMsg);
                        textView.setBackgroundTintList(ContextCompat.getColorStateList(HostRequestActivityG_a.this, R.color.red));
                        textView.setText(response.body().getMessage());
                        Toast toast = new Toast(getApplicationContext());
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.show();
                    }
                }
                binding.pd.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
//ll
            }
        });
    }


    private void permission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if ((ContextCompat.checkSelfPermission(HostRequestActivityG_a.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ||
                    ContextCompat.checkSelfPermission(HostRequestActivityG_a.this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(HostRequestActivityG_a.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES},
                        1);
            }
        } else {
            if ((ContextCompat.checkSelfPermission(HostRequestActivityG_a.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ||
                    ContextCompat.checkSelfPermission(HostRequestActivityG_a.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(HostRequestActivityG_a.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(HostRequestActivityG_a.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == galleryCode && resultCode == RESULT_OK && null != data) {
            selectedImage = null;
            selectedImage = data.getData();
            String[] filePathColumn = {DATA};

            Cursor cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            Glide.with(this).load(selectedImage).centerCrop().into(binding.hostImage);
            picturePath1 = cursor.getString(columnIndex);
            binding.addImage.setVisibility(View.GONE);

            cursor.close();


        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if ((ContextCompat.checkSelfPermission(HostRequestActivityG_a.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ||
                    ContextCompat.checkSelfPermission(HostRequestActivityG_a.this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(HostRequestActivityG_a.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES},
                        1);
            }
        } else {
            if ((ContextCompat.checkSelfPermission(HostRequestActivityG_a.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ||
                    ContextCompat.checkSelfPermission(HostRequestActivityG_a.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(HostRequestActivityG_a.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(HostRequestActivityG_a.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        }

    }

    public void onClickBack(View view) {
        onBackPressed();
    }
}