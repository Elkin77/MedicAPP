package com.medicapp.medicappprojectcomp.servicies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import androidx.fragment.app.Fragment;

import javax.inject.Inject;

import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import lombok.Getter;

@Getter
@Module
@InstallIn(ActivityComponent.class)
public class FileServices {

    public static final String TAG = CameraService.class.getName();
    @Getter
    private String nameFile;
    @Getter
    private Uri fileURI;
    private static final int PICK_FILE_REQUEST_CODE = 1;
    Context context;

    @Inject
    public FileServices(@ApplicationContext Context context) {
        this.context = context;
    }



    public void startFiles(Fragment activity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        activity.startActivityForResult(intent,PermissionService.PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
    }

    public String getFileName(Uri uri) {
        String fileName = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex);
                }
                cursor.close();
            }
        }
        if (fileName == null) {
            fileName = uri.getLastPathSegment();
        }
        return fileName;
    }


}
