package com.hi.live.activity;

import static android.provider.MediaStore.MediaColumns.DATA;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.databinding.ActivitySupportBinding;
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

public class SupportActivityG_a extends BaseActivity_a {

    private static final int GALLERY_CODE = 1001;
    private static final int PERMISSION_REQUEST_CODE = 101;
    ActivitySupportBinding binding;
    SessionManager__a sessionManager;
    String userId;
    Uri selectedImage;
    String picturePath;
    private static final int PERMISSION_REQ_ID = 22;

    private static final int REQ_ID = 1;
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public static String[] storge_permissions_33 = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_support);
        sessionManager = new SessionManager__a(this);
        userId = sessionManager.getUser().getId();
        initmain();
    }

    private void initmain() {
        opengallery();
    }

    private void opengallery() {

        binding.btnOpenGallery.setOnClickListener(v -> {
//            if (checkPermission()) {
//                Intent i = new Intent(Intent.ACTION_PICK,
//                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(i, GALLERY_CODE);
//            } else {
//                requestPermission();
//            }


            choosePhoto();

        });


    }

    private void choosePhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.e("TAG", "onCreate: >>>>>>>>>>>>>  11 ");

            if (checkSelfPermission(storge_permissions_33[0], REQ_ID) && checkSelfPermission(storge_permissions_33[1], REQ_ID) && checkSelfPermission(storge_permissions_33[2], REQ_ID) && checkSelfPermission(storge_permissions_33[3], REQ_ID) && checkSelfPermission(storge_permissions_33[4], REQ_ID)) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, GALLERY_CODE);
            } else {
                ActivityCompat.requestPermissions(this, storge_permissions_33, REQ_ID);
            }

        } else {
            Log.e("TAG", "onCreate: >>>>>>>>>>>>>  22 ");
            if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                    checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                    checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID) && checkSelfPermission(REQUESTED_PERMISSIONS[3], PERMISSION_REQ_ID)) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, GALLERY_CODE);
            } else {

                ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);

            }
        }
    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            //  ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }
        return true;
    }


    private void showLongToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQ_ID) {

            if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[2] != PackageManager.PERMISSION_GRANTED || grantResults[3] != PackageManager.PERMISSION_GRANTED) {
                Log.e("TAG", "onRequestPermissionsResult: 11 ");

                showLongToast("Need permissions " + Manifest.permission.WRITE_EXTERNAL_STORAGE);
                return;
            }
            choosePhoto();
        } else {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[2] != PackageManager.PERMISSION_GRANTED || grantResults[3] != PackageManager.PERMISSION_GRANTED || grantResults[4] != PackageManager.PERMISSION_GRANTED) {
                Log.e("TAG", "onRequestPermissionsResult: 22 ");

                showLongToast("Need permissions " +
                        "/" + Manifest.permission.WRITE_EXTERNAL_STORAGE);

                return;
            }
            choosePhoto();
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && null != data) {

            selectedImage = data.getData();

            Glide.with(this)
                    .load(selectedImage)
                    .circleCrop()
                    .into(binding.image);
            String[] filePathColumn = {DATA};

            Cursor cursor = this.getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();


        }

    }

    public void onClickBack(View view) {
        finish();
    }

    public void onClickSubmit(View view) {
        binding.submit.setEnabled(false);
        String message = binding.etMessage.getText().toString().trim();
        String contact = binding.etContact.getText().toString().trim();
        if (message.equals("")) {
            binding.etMessage.setError("Required!");
            return;
        } else if (contact.equals("")) {
            binding.etContact.setError("Required!");
            return;
        }else {
            RequestBody messagebody = RequestBody.create(MediaType.parse("text/plain"), message);
            RequestBody contactbody = RequestBody.create(MediaType.parse("text/plain"), contact);
            RequestBody userIdbody = RequestBody.create(MediaType.parse("text/plain"), userId);
            HashMap<String, RequestBody> map = new HashMap<>();
            MultipartBody.Part body = null;
            Call<RestResponse> call = null;
            if (picturePath != null) {
                File file = new File(picturePath);
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("multipart/form-data"), file);
                body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);


            } else {
                // ll
            }
            call = RetrofitBuilder_a.create().addSupport(Const_a.DEVKEY, map, body);

            map.put("message", messagebody);
            map.put("contact", contactbody);
            map.put("user_id", userIdbody);
            binding.animationView.setVisibility(View.VISIBLE);
            call.enqueue(new Callback<RestResponse>() {
                @Override
                public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                    if (response.code() == 200) {
                        if (response.body().isStatus()) {
                            Toast.makeText(SupportActivityG_a.this, "Complain Send Successfully", Toast.LENGTH_SHORT).show();
                            try {
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(SupportActivityG_a.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SupportActivityG_a.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                    binding.submit.setEnabled(true);
                    binding.animationView.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<RestResponse> call, Throwable t) {
                    Log.d("TAG", "onFailure: " + t.getMessage());
                }
            });
        }
    }
}