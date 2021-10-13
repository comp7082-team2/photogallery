package com.example.photogallery.Models;

import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PhotoRepository implements IPhotoRepository {

    private static final String TAG = PhotoRepository.class.getName();

    /**
     * Used for threshold comparison of latitude and longitude.
     */
    private static final double THRESHOLD = 0.0001;

    private List<Photo> photos;

    @Override
    public List<Photo> findPhotos(Date startTimestamp, Date endTimestamp, String keywords, String latitude, String longitude) {
        loadPhotos();
        return this.photos.stream()
                // Filter for photos that have a date stamp that comes after the startTimestamp
                .filter(photo -> startTimestamp == null || startTimestamp.before(photo.getDate()))
                // Filter for photos that have a date stamp that comes before the endTimestamp
                .filter(photo -> endTimestamp == null || endTimestamp.after(photo.getDate()))
                // Filter for photos with a caption that contains search keywords
                .filter(photo -> keywords == null || keywords.isEmpty() || photo.getCaption().contains(keywords))
                // Filter for photos that match the provided latitude and/or longitude
                .filter(photo -> {
                    try {
                        float[] latAndLong = new float[2];
                        ExifInterface exif = new ExifInterface(photo.getPhotoFile().getPath());
                        if (exif.getLatLong(latAndLong)) {
                            boolean latMatches = true,
                                    longMatches = true;
                            if(latitude != null && !latitude.isEmpty()) {
                                float lat = Float.parseFloat(latitude);
                                latMatches = Math.abs(lat - latAndLong[0]) < THRESHOLD;
                            }
                            if(longitude != null && !longitude.isEmpty()) {
                                float lng = Float.parseFloat(longitude);
                                longMatches = Math.abs(lng - latAndLong[1]) < THRESHOLD;
                            }
                            return latMatches && longMatches;
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                    return true;
                })
                // Sort by Date created to provide consistent return order since File.listFiles does not guarantee any order
                .sorted(Comparator.comparing(Photo::getDate))
                .collect(Collectors.toList());
    }

    /**
     * Loads photos from storage.
     */
    private void loadPhotos() {
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath(), "/Android/data/com.example.photogallery/files/Pictures");
        ArrayList<String> photos = new ArrayList<String>();
        File[] fList = file.listFiles();
        if (fList == null) {
            this.photos = new ArrayList<>();
        } else {
            this.photos = Arrays.asList(fList)
                    .stream()
                    .map(f -> new Photo(f))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void save(Photo photo) {
        photos.add(photo);
    }
}
