package com.example.medicalappadmin.PenDriver;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class PermissionsHelper {

    private static String perms[] = {
            Manifest.permission.INTERNET,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_CONNECT
    };

    private static SmartPenPermissionListener permissionListener;

    protected static void setLauncher(AppCompatActivity activity){
        launcher = activity.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), isGranted->{
                    for (String perm: perms){
                        if (Boolean.FALSE.equals(isGranted.get(perm))){
                            //Todo: Explain for what purpose we need this permission
                            if (permissionListener != null)
                                permissionListener.onPermission(false);
                        }
                    }
                    if (permissionListener != null)
                        permissionListener.onPermission(true);
                }
        );
    }

    private static ActivityResultLauncher<String[]> launcher;

    protected interface SmartPenPermissionListener{
        void onPermission(boolean isPermitted);
    }

    protected static boolean haveRequiredPermissions(Context context){
        boolean havePerm = true;
        for (String perm: perms){
            if (ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_DENIED){
                havePerm = false;
            };
        }
        return havePerm;
    }

    protected static void requestPermission(AppCompatActivity activity, SmartPenPermissionListener listener){
        permissionListener = listener;
        if (!haveRequiredPermissions(activity)){
            launcher.launch(perms);
        } else {
            listener.onPermission(true);
        }
    }
}
