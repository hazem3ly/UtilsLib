package net.corpy.permissionslib;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import java.io.Serializable;


/**
 * PermissionsFile Class
 * <p>
 * Helper Class Contains Permissions Request and check Methods
 * <p>
 * Version 1.0
 * <p>
 * Updated Version --
 */

//@SuppressWarnings("All")
public class PermissionsFile {

    public static final String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String READ_CALENDAR = Manifest.permission.READ_CALENDAR;
    public static final String WRITE_CALENDAR = Manifest.permission.WRITE_CALENDAR;
    public static final String CAMERA = Manifest.permission.CAMERA;
    public static final String READ_CONTACTS = Manifest.permission.READ_CONTACTS;
    public static final String WRITE_CONTACTS = Manifest.permission.WRITE_CONTACTS;
    public static final String GET_ACCOUNTS = Manifest.permission.GET_ACCOUNTS;
    public static final String RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    public static final String CALL_PHONE = Manifest.permission.CALL_PHONE;
    public static final String READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    public static final String READ_CALL_LOG = Manifest.permission.READ_CALL_LOG;
    public static final String WRITE_CALL_LOG = Manifest.permission.WRITE_CALL_LOG;
    public static final String USE_SIP = Manifest.permission.USE_SIP;
    public static final String BODY_SENSORS = Manifest.permission.BODY_SENSORS;
    public static final String SEND_SMS = Manifest.permission.SEND_SMS;
    public static final String RECEIVE_SMS = Manifest.permission.RECEIVE_SMS;
    public static final String READ_SMS = Manifest.permission.READ_SMS;
    public static final String RECEIVE_WAP_PUSH = Manifest.permission.RECEIVE_WAP_PUSH;
    public static final String RECEIVE_MMS = Manifest.permission.RECEIVE_MMS;

    /**
     * check for list of permissions if all granted it will return true else it
     * will return false
     *
     * @param context
     * @param permissions the list of permissions wanted to check
     * @return true if all permissions are granted other wise false
     */
    public static boolean checkListOfPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * show list of permission to ask user for
     *
     * @param context
     * @param listener    callback that return the return the request result
     * @param permissions the list of permissions wanted to request
     */
    public static void showListOfPermissions(Context context, PermissionListener listener, String... permissions) {
        PermissionsActivity.permissionListener = listener;
        context.startActivity(new Intent(context, PermissionsActivity.class).putExtra(PermissionsActivity.REQUESTED_PERMISSIONS,
                permissions));
    }

    /**
     * check for location permissions
     *
     * @param context
     * @return
     */
    public static boolean locationPermissionCheck(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * request location permission
     *
     * @param context
     * @param listener
     */
    public static void showLocationDialogPermission(Context context, PermissionListener listener) {
        PermissionsActivity.permissionListener = listener;
        context.startActivity(new Intent(context, PermissionsActivity.class).putExtra(PermissionsActivity.REQUESTED_PERMISSIONS,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                        , Manifest.permission.ACCESS_COARSE_LOCATION}));

    }

    /**
     * check for storage permission
     *
     * @param context
     * @return
     */
    public static boolean StoragePermissionCheck(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * request storage permission
     *
     * @param context
     * @param listener
     */
    public static void showStorageDialogPermission(Context context, PermissionListener listener) {

        PermissionsActivity.permissionListener = listener;
        context.startActivity(new Intent(context, PermissionsActivity.class).putExtra(PermissionsActivity.REQUESTED_PERMISSIONS,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE}));

    }

    /**
     * calender permission check
     *
     * @param context
     * @return
     */
    public static boolean CalenderPermissionCheck(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * request calender permission
     *
     * @param context
     * @param listener
     */
    public static void showCalenderDialogPermission(Context context, PermissionListener listener) {

        PermissionsActivity.permissionListener = listener;
        context.startActivity(new Intent(context, PermissionsActivity.class).putExtra(PermissionsActivity.REQUESTED_PERMISSIONS,
                new String[]{Manifest.permission.READ_CALENDAR
                        , Manifest.permission.WRITE_CALENDAR}));


    }

    /**
     * camera permission check
     *
     * @param context
     * @return
     */
    public static boolean CameraPermissionCheck(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * request camera permission
     *
     * @param context
     * @param listener
     */
    public static void showCameraDialogPermission(Context context, PermissionListener listener) {

        PermissionsActivity.permissionListener = listener;
        context.startActivity(new Intent(context, PermissionsActivity.class).putExtra(PermissionsActivity.REQUESTED_PERMISSIONS,
                new String[]{Manifest.permission.CAMERA}));


    }

    /**
     * contact permission check
     *
     * @param context
     * @return
     */
    public static boolean ContactPermissionCheck(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * request contact permission
     *
     * @param context
     * @param listener
     */
    public static void showContactDialogPermission(Context context, PermissionListener listener) {

        PermissionsActivity.permissionListener = listener;
        context.startActivity(new Intent(context, PermissionsActivity.class).putExtra(PermissionsActivity.REQUESTED_PERMISSIONS,
                new String[]{Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS,
                        Manifest.permission.GET_ACCOUNTS}));


    }

    /**
     * microphone permission check
     *
     * @param context
     * @return
     */
    public static boolean MicroPhonePermissionCheck(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * request microphone permission
     *
     * @param context
     * @param listener
     */
    public static void showMicroPhoneDialogPermission(Context context, PermissionListener listener) {

        PermissionsActivity.permissionListener = listener;
        context.startActivity(new Intent(context, PermissionsActivity.class).putExtra(PermissionsActivity.REQUESTED_PERMISSIONS,
                new String[]{Manifest.permission.RECORD_AUDIO}));


    }

    /**
     * phone permission check
     *
     * @param context
     * @return
     */
    public static boolean PhonePermissionCheck(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALL_LOG) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.USE_SIP) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.PROCESS_OUTGOING_CALLS) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * request phone permission
     *
     * @param context
     * @param listener
     */
    public static void showPhoneDialogPermission(Context context, PermissionListener listener) {

        PermissionsActivity.permissionListener = listener;
        context.startActivity(new Intent(context, PermissionsActivity.class).putExtra(PermissionsActivity.REQUESTED_PERMISSIONS,
                new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.WRITE_CALL_LOG, Manifest.permission.USE_SIP, Manifest.permission.PROCESS_OUTGOING_CALLS}));


    }

    /**
     * sensor permission check
     *
     * @param context
     * @return
     */
    public static boolean SensorsPermissionCheck(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * request sensor permission
     *
     * @param context
     * @param listener
     */
    public static void showSensorsDialogPermission(Context context, PermissionListener listener) {

        PermissionsActivity.permissionListener = listener;
        context.startActivity(new Intent(context, PermissionsActivity.class).putExtra(PermissionsActivity.REQUESTED_PERMISSIONS,
                new String[]{Manifest.permission.BODY_SENSORS}));


    }

    /**
     * sms permission check
     *
     * @param context
     * @return
     */
    public static boolean SMSPermissionCheck(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_WAP_PUSH) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_MMS) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * request sms permission
     *
     * @param context
     * @param listener
     */
    public static void showSMSDialogPermission(Context context, PermissionListener listener) {

        PermissionsActivity.permissionListener = listener;
        context.startActivity(new Intent(context, PermissionsActivity.class).putExtra(PermissionsActivity.REQUESTED_PERMISSIONS,
                new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS,
                        Manifest.permission.RECEIVE_WAP_PUSH, Manifest.permission.RECEIVE_MMS}));

    }

    /**
     * callback interface for permission request result
     */
    public interface PermissionListener extends Serializable {
        void permissionResult(boolean hasPermission);
    }

}
