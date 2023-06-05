package com.medicapp.medicappprojectcomp.servicies;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import javax.inject.Inject;

import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Module
@InstallIn(ActivityComponent.class)
public class PermissionService {

    private static final String TAG = PermissionService.class.getName();
    static public final int PERMISSIONS_REQUEST_CAMERA = 1001;
    static public final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1002;
    static public final int PERMISSIONS_REQUEST_LOCATION = 1003;

    static public final int PERMISSIONS_PHOTO = 1004;
    static public final int PERMISSIONS_VIDEO= 1005;
    static public final int PERMISSIONS_REQUEST_GALLERY = 1002;

    static public final int PERMISSIONS_REQUEST_ALARM = 1010;


    private boolean mCameraPermissionGranted;
    private boolean mReadExternalStoragePermissionGranted;
    private boolean mLocationPermissionGranted;
    private boolean mPhotoPermission;

    private boolean mVideoPermission;

    private boolean mAlarm;

    private Context context;

    @Inject
    public PermissionService(@ApplicationContext Context context) {
        this.context = context;
        mCameraPermissionGranted = checkPermission(Manifest.permission.CAMERA);
        mReadExternalStoragePermissionGranted = checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        mLocationPermissionGranted = checkPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        mAlarm =checkPermission(Manifest.permission.SCHEDULE_EXACT_ALARM);
    }

    public void getCameraPermission(Activity activity) {
        if (checkPermission(Manifest.permission.CAMERA)) {
            mCameraPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
        }
    }

    public void getLocationPermission(Activity activity){
        if(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)){
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
            checkPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }


    private boolean checkPermission(String manifestPermissions) {
       if (ContextCompat.checkSelfPermission(context.getApplicationContext(), manifestPermissions) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermission: permission "+manifestPermissions+" is already granted.");
            return true;
        } else {
            Log.d(TAG, "checkPermission: permission ("+manifestPermissions+") not granted, need to request it.");
            return false;
        }
    }


    public void getReadExternalStoragePermission(Activity activity) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                mReadExternalStoragePermissionGranted = true;
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        }else{
            Intent intent=new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            activity.startActivityForResult(intent,PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    public void getReadPhotosAndVideo(Activity activity) {
        if (checkPermission(Manifest.permission.READ_MEDIA_IMAGES)){
            mPhotoPermission=true;
        }else{
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSIONS_PHOTO);
        }
        if (checkPermission(Manifest.permission.READ_MEDIA_VIDEO) ) {
            mVideoPermission=true;
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_MEDIA_VIDEO}, PERMISSIONS_VIDEO);
        }
    }

    public void getGalleryPermission(Activity activity) {
        if(checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)){
            mReadExternalStoragePermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_GALLERY);
        }
    }

    public void getAlarmPermission(Activity activity) {
        if(checkPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)){
            mAlarm = true;
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SCHEDULE_EXACT_ALARM}, PERMISSIONS_REQUEST_GALLERY);
        }
    }
}

