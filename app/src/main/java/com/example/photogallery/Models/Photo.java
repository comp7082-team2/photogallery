package com.example.photogallery.Models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

public class Photo {

    private static final String TAG = Photo.class.getName();

    private File photoFile;

    public Photo(File photoFile) {
        this.photoFile = photoFile;
    }

    public File getPhotoFile() {
        return photoFile;
    }

    public String getCaption() {
        if (photoFile == null) {
            return null;
        }
        String[] attr = photoFile.getName().split("_");
        return attr[1];
    }


    public void updateCaption(String caption) {
        if (photoFile == null) {
            return;
        }
        String[] attr = photoFile.getAbsolutePath().split("_");
        attr[1] = caption;
        // Rebuild the file path since String.join requires a higher minimum API
        String newFilePath = Arrays.asList(attr)
                .stream()
                .collect(Collectors.joining("_"));
        File from = new File(photoFile.getAbsolutePath());
        File to = new File(newFilePath);
        from.renameTo(to);
        this.photoFile = to;
    }

    public String getDatestamp() {
        if (photoFile == null) {
            return null;
        }
        String[] attr = photoFile.getName().split("_");
        return attr[2];
    }

    public Bitmap getBitmap() {
        if (photoFile == null) {
            return null;
        }
        return BitmapFactory.decodeFile(photoFile.getPath());
    }

    public Date getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String[] attr = photoFile.getAbsolutePath().split("_");
        try {
            return format.parse(attr[2] + "_" + attr[3]);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }
}
