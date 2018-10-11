package com.hazem.utilslib.libs.permissions;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;

public class PermissionsActivity extends Activity {

    public static final String REQUESTED_PERMISSIONS = "permission";
    public static PermissionsFile.PermissionListener permissionListener;
    private String[] permissions;
    private int pCode = 12321;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra(REQUESTED_PERMISSIONS)) {
            permissions = getIntent().getStringArrayExtra(REQUESTED_PERMISSIONS);
            checkPermissions();
        } else {
            finish();
            overridePendingTransition(0, 0);
        }
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            boolean flag = false;
            for (String s : permissions)
                if (checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED)
                    flag = true;

            if (flag) {
                requestPermissions(permissions, pCode);
            } else {
                permissionListener.permissionResult(true);
                finish();
                overridePendingTransition(0, 0);
            }
        } else {
            finish();
            overridePendingTransition(0, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == pCode) {
            boolean flag = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                for (int i = 0, len = permissions.length; i < len; i++)
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        flag = false;
            if (flag) {
                if (permissionListener != null)
                    permissionListener.permissionResult(true);
            } else if (permissionListener != null)
                permissionListener.permissionResult(false);
            finish();
            overridePendingTransition(0, 0);
        }
    }


}



