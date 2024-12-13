package com.hi.live.fregment;


import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.hi.live.R;
import com.hi.live.activity.SearchForNewFriendsActivity_a;
import com.hi.live.databinding.FragmentVideoOfflineBinding;
import com.hi.live.utils.camara.CameraPreview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VideoOfflineFragment_a extends Fragment {
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 7;
    private static final int REQ_ID = 1;

    private static final String TAG = "camarafeag";
    FragmentVideoOfflineBinding binding;
    Camera mCamera;
    CameraPreview mPreview;
    Dialog dialog;
    List<String> listPermissionsNeeded = new ArrayList<>();

    private static final int PERMISSION_REQ_ID = 22;
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

    public VideoOfflineFragment_a() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_video_offline, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        permissionCheckAboveAndroid13();
        initview();
    }

    private void permissionCheckAboveAndroid13() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.e("TAG", "onCreate: >>>>>>>>>>>>>  11 ");
            if (checkSelfPermission(storge_permissions_33[0], REQ_ID) && checkSelfPermission(storge_permissions_33[1], REQ_ID) && checkSelfPermission(storge_permissions_33[2], REQ_ID) && checkSelfPermission(storge_permissions_33[3], REQ_ID) && checkSelfPermission(storge_permissions_33[4], REQ_ID)) {
            } else {
                ActivityCompat.requestPermissions(getActivity(), storge_permissions_33, REQ_ID);
            }

        } else {
            Log.e("TAG", "onCreate: >>>>>>>>>>>>>  22 ");
            if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                    checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                    checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID) && checkSelfPermission(REQUESTED_PERMISSIONS[3], PERMISSION_REQ_ID)) {
            } else {
                ActivityCompat.requestPermissions(getActivity(), REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
            }
        }
    }

    private void initview() {
        binding.ivMatch.setOnClickListener(v -> startActivity(new Intent(getActivity(), SearchForNewFriendsActivity_a.class)));
    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(getActivity(), permission) !=
                PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }

        return true;
    }

    private void setCamara() {
        mCamera = getCameraInstance();
        if (mCamera != null) {
            mCamera.setDisplayOrientation(90);
            mPreview = new CameraPreview(requireActivity(), mCamera);
            binding.cameraPreview.addView(mPreview);
        } else {
            Toast.makeText(getActivity(), "No Camara Detected!!!", Toast.LENGTH_SHORT).show();
        }
    }

    public Camera getCameraInstance() {
        Camera c = null;
        try {
            c = openFrontFacingCamera(); // attempt to get a Camera instance// attempt to get a Camera instance
        } catch (Exception e) {
            Log.d(TAG, "getCameraInstance: " + e.getMessage());
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private Camera openFrontFacingCamera() {
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        Log.d(TAG, "openFrontFacingCamera: num  " + cameraCount);
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }
        return cam;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {
            Map<String, Integer> perms = new HashMap<>();
            // Initialize the map with both permissions
            perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            // Fill with actual results from user
            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for both permissions
                if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    Log.d("in fragment on request", "CAMERA & WRITE_EXTERNAL_STORAGE READ_EXTERNAL_STORAGE permission granted");
                    // process the normal flow
                    //else any one or both the permissions are not granted
                } else {
                    Log.d("in fragment on request", "Some permissions are not granted ask again ");
                    //permission is denied (getActivity() is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                    //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.RECORD_AUDIO)) {
                        showDialogOK("Camera and Storage Permission required for getActivity() app",
                                (dialog, which) -> {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            permissionCheckAboveAndroid13();
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            // proceed with logic by disabling the related features or quit the app.
                                            break;
                                    }
                                });
                    }
                    //permission is denied (and never ask again is  checked)
                    //shouldShowRequestPermissionRationale will return false
                    else {
                        Toast.makeText(getActivity(), "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                .show();
                        //                            //proceed with logic by disabling the related features or quit the app.
                    }
                }
            }
        } else {
            Map<String, Integer> perms = new HashMap<>();
            // Initialize the map with both permissions
            perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.POST_NOTIFICATIONS, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.READ_MEDIA_VIDEO, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.READ_MEDIA_IMAGES, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.READ_MEDIA_AUDIO, PackageManager.PERMISSION_GRANTED);
            // Fill with actual results from user
            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for both permissions
                if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    Log.d("in fragment on request", "CAMERA & WRITE_EXTERNAL_STORAGE READ_EXTERNAL_STORAGE permission granted");
                    // process the normal flow
                    //else any one or both the permissions are not granted
                } else {
                    Log.d("in fragment on request", "Some permissions are not granted ask again ");
                    //permission is denied (getActivity() is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                    //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_MEDIA_AUDIO) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_MEDIA_VIDEO) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_MEDIA_IMAGES) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.POST_NOTIFICATIONS)) {
                        showDialogOK("Camera and Storage Permission required for getActivity() app",
                                (dialog, which) -> {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            permissionCheckAboveAndroid13();
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            // proceed with logic by disabling the related features or quit the app.
                                            break;
                                    }
                                });
                    }
                    //permission is denied (and never ask again is  checked)
                    //shouldShowRequestPermissionRationale will return false
                    else {
                        Toast.makeText(getActivity(), "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                .show();
                        //                            //proceed with logic by disabling the related features or quit the app.
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler(Looper.getMainLooper()).postDelayed(this::setCamara, 1000);
    }
}