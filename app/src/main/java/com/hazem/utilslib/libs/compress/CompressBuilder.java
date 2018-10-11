package com.hazem.utilslib.libs.compress;

import android.graphics.Bitmap;

import java.io.File;

class CompressBuilder {

    int maxSize;

    int maxWidth;

    int maxHeight;

    File cacheDir;

    Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;

    int gear = Compressor.THIRD_GEAR;

    CompressBuilder(File cacheDir) {
        this.cacheDir = cacheDir;
    }

}
