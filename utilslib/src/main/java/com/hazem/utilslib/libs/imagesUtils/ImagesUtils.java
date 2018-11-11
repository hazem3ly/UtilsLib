package com.hazem.utilslib.libs.imagesUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.hazem.utilslib.BuildConfig;
import com.hazem.utilslib.libs.compress.Compressor;
import com.hazem.utilslib.libs.compress.OnCompressListener;

import net.corpy.permissionslib.PermissionsFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * ImagesUtils Class
 * <p>
 * Helper Class Contains Images Processing Methods
 * <p>
 * Version 1.1
 * <p>
 * Updated Version 1
 */

@SuppressLint("NewApi")
@SuppressWarnings("unused")
public class ImagesUtils {

    /**
     * method take file uri path and return the file name
     *
     * @param context application context
     * @param uri     requested file uri
     * @return file name
     */
    public static String getFileNameFromUri(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null,
                    null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    /**
     * method that take imageFile path and return the file uri
     *
     * @param context  application context
     * @param filePath the image file path
     * @return file uri
     */
    public static Uri getUriFromFilePath(Context context, String filePath) {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            File file = new File(filePath);
            if (file.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * method returns the real file path on the storage from it's uri
     *
     * @param context application context
     * @param uri     requested file uri
     * @return file real path
     */
    public static String getFilePathFromUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if (isCustomProvider(context, uri)) {
            File file = new File(Environment.getExternalStorageDirectory(), uri.getLastPathSegment());
            if (file.exists()) return file.getAbsolutePath();
//            File file = new File(uri.getPath());
        }

        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        // final try
        else {
            Cursor cursor = null;
            try {
                String[] proj = {MediaStore.Images.Media.DATA};
                cursor = context.getContentResolver().query(uri, proj, null,
                        null, null);
                int column_index;
                if (cursor != null) {
                    column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    return cursor.getString(column_index);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        return null;
    }


    /**
     * get the current bitmap from it's uri
     *
     * @param context         application context
     * @param selectedFileUri requested file uri
     * @return the bitmap image
     */
    public static Bitmap uriToBitmap(Context context, Uri selectedFileUri) {
        Bitmap bitmap = null;
        if (context == null || selectedFileUri == null) {
            return null;
        }
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    context.getContentResolver().openFileDescriptor(selectedFileUri, "r");
            if (parcelFileDescriptor != null) {
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            }

            if (bitmap == null) {
                bitmap = BitmapFactory.decodeStream(context
                        .getContentResolver().openInputStream(selectedFileUri));
            }

            if (bitmap == null) {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(),
                        Uri.fromFile(new File(selectedFileUri.getPath())));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * copy file from src file to destination file
     *
     * @param src source file want to copy from
     * @param dst destination file want to copy to
     */
    public static void copyFile(File src, File dst) {
        try {
            InputStream in = new FileInputStream(src);
            if (!dst.getParentFile().exists())
                if (!dst.getParentFile().mkdirs()) return;
            if (!dst.exists())
                if (!dst.createNewFile()) return;
            OutputStream out = new FileOutputStream(dst);
            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * method that take imageFile and return the file uri path
     *
     * @param context   application context
     * @param imageFile the image file requested
     * @return file uri
     */
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * method returns the file on the storage from it's uri
     *
     * @param context application context
     * @param uri     requested file uri
     * @return file
     */
    public static File getFileFromUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return new File(Environment.getExternalStorageDirectory() + "/" + split[1]);
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return new File(getDataColumn(context, contentUri, null, null));
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return new File(getDataColumn(context, contentUri, selection, selectionArgs));
            }
        } else if (isCustomProvider(context, uri)) {
            File file = new File(Environment.getExternalStorageDirectory(), uri.getLastPathSegment());
            if (file.exists()) return file;
//            File file = new File(uri.getPath());
        }

        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return new File(getDataColumn(context, uri, null, null));
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return new File(uri.getPath());
        }
        // final try
        else {
            Cursor cursor = null;
            try {
                String[] proj = {MediaStore.Images.Media.DATA};
                cursor = context.getContentResolver().query(uri, proj, null,
                        null, null);
                int column_index;
                if (cursor != null) {
                    column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    return new File(cursor.getString(column_index));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        return null;
    }

    /**
     * method that take file length and return the file size formatted in string
     *
     * @param context    application context
     * @param fileLength the file length
     * @return formatted file size
     */
    public static String getFileSizeInString(Context context, long fileLength) {
        return android.text.format.Formatter.formatShortFileSize(context, fileLength);
    }

    /**
     * convert bitmap file into bytes array
     *
     * @param bitmap bitmap file want to convert
     * @return file bytes array
     */
    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = null;
        try {
            stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stream.toByteArray();
    }

    /**
     * convert bytes array into bitmap file
     *
     * @param imageByteArray the file bytes array
     * @return the converted file
     */
    public static Bitmap getImageFromByteArray(byte[] imageByteArray) {
        return BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
    }

    /**
     * helper method used to detect file path form uri
     *
     * @param context       application context
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * helper method used while detecting file path from uri that check
     * if the given uri is document file
     *
     * @param uri
     * @return
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * helper method used while detecting file path from uri that check
     * if the given uri is document file from external storage
     *
     * @param uri
     * @return
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * helper method used while detecting file path from uri that check
     * if the given uri is document file
     *
     * @param uri
     * @return
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isCustomProvider(Context context, Uri uri) {
        return (context.getApplicationContext().getPackageName() + ".provider").equals(uri.getAuthority());
    }

    /**
     * method that allow you to zoom any image with opening transparent dialog with the gaven view
     *
     * @param activity  the current opened activity to open the dialog above
     * @param imageView the view wanted to zoom
     */
    public static void showZoomDialog(Activity activity, ImageView imageView) {
        if (imageView == null || activity == null || activity.isFinishing()) return;
        Drawable drawable = imageView.getDrawable();
        final Dialog dialog = new Dialog(activity, android.R.style.Theme_Holo_Dialog_NoActionBar);

        FrameLayout frameLayout = new FrameLayout(activity);
        frameLayout.setBackgroundColor(activity.getResources().getColor(android.R.color.white));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        frameLayout.setLayoutParams(layoutParams);


        ImageView image = new ImageView(activity);
        FrameLayout.LayoutParams imglayoutParams = new FrameLayout.LayoutParams
                (600, 600);
        image.setLayoutParams(imglayoutParams);
        image.setScaleType(ImageView.ScaleType.FIT_XY);
        image.setImageDrawable(drawable);

        ImageButton imageButton = new ImageButton(activity);
        FrameLayout.LayoutParams buttonlayoutParams = new FrameLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonlayoutParams.gravity = Gravity.END;
        imageButton.setLayoutParams(buttonlayoutParams);
        imageButton.setBackgroundResource(android.R.drawable.ic_menu_close_clear_cancel);
        frameLayout.addView(image);
        frameLayout.addView(imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(frameLayout);
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
//        dialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
    }

    /**
     * and the needed image size to compress into
     * compresses an image using its path
     *
     * @param context            application context
     * @param imageFile          the file wanted to compress
     * @param outputFileSizeKB   the max size of compressing
     * @param compressorCallBack callback that return the compress result
     */
    public static void compressImage(Context context,
                                     File imageFile, int outputFileSizeKB,
                                     final CompressorCallBack compressorCallBack) {
        if (imageFile == null || compressorCallBack == null) return;
        Compressor.compress(context, imageFile)
                .setMaxSize(outputFileSizeKB)
                .putGear(Compressor.CUSTOM_GEAR)
                .launch(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        compressorCallBack.onStartCompress();
                    }

                    @Override
                    public void onSuccess(File file) {
                        compressorCallBack.onSuccess(file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        compressorCallBack.onError(e);
                    }
                });

    }


    /**
     * check if given file with directory and name is exist
     *
     * @param directoryPath where the file is saved
     * @param fileName      the file name
     * @param fileExtension file extension
     * @return true if file exist other wise return false
     */
    public static boolean checkFileExist(String directoryPath, String fileName, String fileExtension) {

        File folder = createFolder(directoryPath);
        if (folder != null) {
            File createdFile = new File(folder, fileName + "." + fileExtension);
            return createdFile.exists();
        }
        return false;
    }

    /**
     * helper method to create directory at run time
     *
     * @param folderName the wanted folder to create
     * @return the new created file
     */
    public static File createFolder(String folderName) {
        if (Environment.getExternalStorageState() == null) {
            File file = new File(Environment.getDataDirectory()
                    + "/" + folderName + "/");
            if (file.exists()) return file;
            else if (file.mkdirs()) return file;

        } else if (Environment.getExternalStorageState() != null) {
            File file = new File(Environment.getExternalStorageDirectory()
                    + "/" + folderName + "/");
            if (file.exists()) return file;
            else if (file.mkdirs()) return file;
        }
        return null;
    }

    /**
     * create files method
     *
     * @param fileDirectory the directory want to create file on
     * @param fileName      the created file name
     * @param fileExtension the created file extension
     * @return the created file
     */
    public static File createFile(String fileDirectory, String fileName, String fileExtension) {

        File createdFile = null;
        try {
            File folder = createFolder(fileDirectory);
            if (folder != null) {
                createdFile = new File(folder, fileName + "." + fileExtension);
                if (createdFile.exists()) return createdFile;
                else if (createdFile.createNewFile()) return createdFile;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return createdFile;
    }

    /**
     * helper method used to save bitmap image into given directory with given name and extension
     *
     * @param bitmap        the image wanted to save
     * @param savePath      the location want to save file in
     * @param imageName     the file name
     * @param fileExtension the file extension want to save file with
     * @return the created file
     */
    public static File saveImageFile(Bitmap bitmap, String savePath,
                                     String imageName, String fileExtension) {
        File destination = null;

        String folder = checkPathExistOrCreate(savePath);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            destination = new File(folder + "/" + imageName + "." + fileExtension);
            FileOutputStream fo;
            try {
                if (destination.createNewFile()) {
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return destination;

    }

    public static File saveBytesImageFile(byte[] image, String savePath,
                                          String imageName, String fileExtension) {
        File destination = null;

        String folder = checkPathExistOrCreate(savePath);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        if (image != null && image.length > 0) {

            destination = new File(folder + "/" + imageName + "." + fileExtension);

            OutputStream os = null;
            try {
                os = new FileOutputStream(destination);
                os.write(image);
                os.close();
            } catch (IOException e) {
                Log.w("", "Cannot write to ", e);
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        // Ignore
                    }
                }
            }
        }
        return destination;

    }

    public static Uri saveDrawableToFile(Context context, int drawableRes, String savePath,
                                         String imageName, String fileExtension) {
        File destination = null;

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableRes);
        String folder = checkPathExistOrCreate(savePath);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            destination = new File(folder + "/" + imageName + "." + fileExtension);
            FileOutputStream fo;
            try {
                if (destination.createNewFile()) {
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (destination != null) {
            return getUriFromFilePath(context, (destination.getAbsolutePath()));
        }
        return null;
    }

    public static void shareImage(Context context, Uri imageUri) {
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);

        intentShareFile.setType("image/*");
        intentShareFile.putExtra(Intent.EXTRA_STREAM, imageUri);

        intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                "Sharing Image...");
        intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing Image...");

        context.startActivity(Intent.createChooser(intentShareFile, "Share Image"));
    }

    public static File saveImageFile(File imageFile, String savePath,
                                     String imageName, String fileExtension) {
        File destination = null;

        String folder = checkPathExistOrCreate(savePath);

        if (imageFile != null) {
            destination = new File(folder + "/" + imageName + "." + fileExtension);
            copyFile(imageFile, destination);
        }
        return destination;

    }

    /**
     * method that check if the given path is exist and if not exist create this path
     *
     * @param savePath the requested path
     * @return return the file absolute path
     */
    private static String checkPathExistOrCreate(String savePath) {
        File file = new File(savePath);
        if (!file.exists()) {
            File file1 = (createFolder(savePath));
            if (file1 != null) {
                return file1.getAbsolutePath();
            }
        } else return file.getAbsolutePath();

        return null;
    }

    /**
     * some mobiles camera make capture images too large to be uploaded into a texture of
     * image view , so this helper method used to scale down the image to be uploaded to the
     * image view easily
     *
     * @param imgView the imageView wanted to upload image to
     * @param imgFile the image file
     */
    public static void setImageToImageView(final ImageView imgView, File imgFile) {

        /* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

        /* Get the size of the ImageView */
        int targetW = imgView.getWidth();
        int targetH = imgView.getHeight();

        /* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgFile.getAbsolutePath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        /* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

        /* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        /* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), bmOptions);

        /* Associate the Bitmap to the ImageView */
        imgView.setImageBitmap(bitmap);
    }

    public static void startSelectImageIntent(final Activity activity, final OnChooserFileResult chooserImageResultCallback) {

        if (!PermissionsFile.StoragePermissionCheck(activity)) {
            PermissionsFile.showStorageDialogPermission(activity, new PermissionsFile.PermissionListener() {
                @Override
                public void permissionResult(boolean hasPermission) {
                    if (hasPermission) galleryIntent(activity, chooserImageResultCallback);
                    else {
                        Toast.makeText(activity, "Permission Needed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            galleryIntent(activity, chooserImageResultCallback);
        }
    }

    public static void startSelectFileIntent(final Activity activity, final OnChooserFileResult chooserImageResultCallback) {

        if (!PermissionsFile.StoragePermissionCheck(activity)) {
            PermissionsFile.showStorageDialogPermission(activity, new PermissionsFile.PermissionListener() {
                @Override
                public void permissionResult(boolean hasPermission) {
                    if (hasPermission) fileIntent(activity, chooserImageResultCallback);
                    else {
                        Toast.makeText(activity, "Permission Needed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            fileIntent(activity, chooserImageResultCallback);
        }
    }

    public static void startCameraCaptureIntent(final Activity activity, final OnChooserFileResult chooserImageResult) {

        if (!PermissionsFile.CameraPermissionCheck(activity)) {
            PermissionsFile.showCameraDialogPermission(activity, new PermissionsFile.PermissionListener() {
                @Override
                public void permissionResult(boolean hasPermission) {
                    if (hasPermission) cameraIntent(activity, chooserImageResult);
                    else {
                        Toast.makeText(activity, "Permission Needed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            cameraIntent(activity, chooserImageResult);
        }

    }

    private static void cameraIntent(Activity activity, OnChooserFileResult chooserImageResult) {
        ChooserActivity.chooserImageResultCallback = chooserImageResult;
        activity.startActivity(new Intent(activity, ChooserActivity.class)
                .putExtra(ChooserActivity.TYPE, ChooserActivity.CAPTURE));
    }

    private static void galleryIntent(Activity activity, OnChooserFileResult chooserImageResultCallback) {
        ChooserActivity.chooserImageResultCallback = chooserImageResultCallback;
        activity.startActivity(new Intent(activity, ChooserActivity.class)
                .putExtra(ChooserActivity.TYPE, ChooserActivity.IMAGE));
    }

    private static void fileIntent(Activity activity, OnChooserFileResult chooserImageResultCallback) {
        ChooserActivity.chooserImageResultCallback = chooserImageResultCallback;
        activity.startActivity(new Intent(activity, ChooserActivity.class)
                .putExtra(ChooserActivity.TYPE, ChooserActivity.FILE));
    }

    public static Uri createUriCashFileForDrawable(Context context, int drawableRes) {
        Uri uri;
        try {

            File file = createCashFileForDrawable(context, drawableRes);
            if (file == null) return null;
            uri = FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID + ".provider", file);

        } catch (android.content.ActivityNotFoundException ex) {
            return null;
        }
        return uri;
    }

    public static Bitmap getBitmapFromDrawable(Context context, int drawableRes) {
        return BitmapFactory.decodeResource(context.getResources(), drawableRes);
    }

    public static File createCashFile(Context context, Bitmap bitmap) {
        //save bitmap to app cache folder
        File outputFile = new File(context.getCacheDir(), "temp.jpg");
        FileOutputStream outPutStream;
        try {
            outPutStream = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outPutStream);
            outPutStream.flush();
            outPutStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return outputFile;
    }

    private static File createCashFileForDrawable(Context context, int drawableRes) {
        //convert drawable resource to bitmap
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableRes);

        //save bitmap to app cache folder
        File outputFile = new File(context.getCacheDir(), "share.png");
        FileOutputStream outPutStream;
        try {
            outPutStream = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outPutStream);
            outPutStream.flush();
            outPutStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return outputFile;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public interface OnChooserFileResult {
        void OnResult(String filePath);
    }


    /**
     * Callback interface used to notify what the current state of compressing process and return
     * the compressed image
     */
    public interface CompressorCallBack {
        void onStartCompress();

        void onSuccess(File file);

        void onError(Throwable e);
    }

}
