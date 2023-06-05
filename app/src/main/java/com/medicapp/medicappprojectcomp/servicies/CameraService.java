package com.medicapp.medicappprojectcomp.servicies;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.medicapp.medicappprojectcomp.activities.BaseActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import lombok.Getter;

@Getter
@Module
@InstallIn(ActivityComponent.class)
public class CameraService {

    public static final String TAG = CameraService.class.getName();
    @Getter
    private String photoName;
    @Getter
    private Uri photoURI;

    Context context;

    @Inject
    public CameraService(@ApplicationContext Context context) {
        this.context = context;
    }

    public void startCamera(Fragment activity) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
            Log.e(TAG, ex.getMessage());
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            photoURI = FileProvider.getUriForFile(this.context,
                    "com.medicapp.medicappprojectcomp.fileprovider",
                    photoFile);
            photoName = photoFile.getName();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            activity.startActivityForResult(takePictureIntent, PermissionService.PERMISSIONS_REQUEST_CAMERA);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "CAMARA_" + timeStamp + "_";
        File storageDir = this.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        String currentPhotoPath = image.getAbsolutePath();
        Log.i(TAG, String.format("Path: %s", currentPhotoPath));
        return image;
    }

    public void startGallery(BaseActivity activity) {
        Intent pickGalleryImage = new Intent(Intent.ACTION_PICK);
        pickGalleryImage.setType("image/*");
        activity.startActivityForResult(pickGalleryImage, PermissionService.PERMISSIONS_REQUEST_GALLERY);
    }
}
