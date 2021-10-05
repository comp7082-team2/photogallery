package com.example.photogallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.BitmapFactory;

import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int SEARCH_ACTIVITY_REQUEST_CODE = 2;
    String mCurrentPhotoPath;
    private ArrayList<String> photos = null;
    private int index = 0;
    private FusedLocationProviderClient fusedLocationClient;
    private static StringBuilder sb = new StringBuilder(20);
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private double latitude = 0.0;
    private double longitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        photos = findPhotos(new Date(Long.MIN_VALUE), new Date(), null, null, "");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (photos.size() == 0) {
            displayPhoto(null);
        } else {
            displayPhoto(photos.get(index));
        }

        // creates location request object
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(30000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // updates last location on request
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        };
    }

    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    protected void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    public void setExif(String filename) {
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    try {
                        ExifInterface exif = new ExifInterface(filename);
                        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, latitudeRef(latitude));
                        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, longitudeRef(longitude));
                        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, convert(latitude));
                        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, convert(longitude));
                        exif.saveAttributes();
                    } catch (IOException e) {
                        // error
                    }
                }
            }
        });
    }

    // converts latitude reference from decimal for DMS (degree minute second) format
    public static String latitudeRef(double latitude) {
        return latitude<0.0d?"S":"N";
    }

    // converts longitude reference from decimal for DMS (degree minute second) format
    public static String longitudeRef(double longitude) {
        return longitude<0.0d?"W":"E";
    }

    // convert latitude or longitude into DMS (degree minute second) format.
    synchronized public static final String convert(double lat_long) {
        lat_long = Math.round(lat_long*10000.0)/10000.0;
        lat_long=Math.abs(lat_long);
        int degree = (int) lat_long;
        lat_long *= 60;
        lat_long -= (degree * 60.0d);
        int minute = (int) lat_long;
        lat_long *= 60;
        lat_long -= (minute * 60.0d);
        int second = (int) (lat_long*1000.0d);

        sb.setLength(0);
        sb.append(degree);
        sb.append("/1,");
        sb.append(minute);
        sb.append("/1,");
        sb.append(second);
        sb.append("/1000");
        return sb.toString();
    }

    public void takePhoto(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;

        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // error
        }

        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this, "com.example.photogallery.fileprovider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private ArrayList<String> findPhotos(Date startTimestamp, Date endTimestamp, String latitude, String longitude, String keywords) {
        File file = new File(Environment.getExternalStorageDirectory()
            .getAbsolutePath(), "/Android/data/com.example.photogallery/files/Pictures");
        ArrayList<String> photos = new ArrayList<String>();
        File[] fList = file.listFiles();
        if (fList != null) {
            for (File f : fList) {
                //check exif lat & long
                ExifInterface exif = null;
                String exifLatitude, exifLatitude_ref, exifLongitude, exifLongitude_ref;
                try {
                    exif = new ExifInterface(f.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                exifLatitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                exifLatitude_ref = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                exifLongitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                exifLongitude_ref = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

                boolean geoBool;
                boolean longitudeBool, longitudeBoolRef, latitudeBool, latitudeBoolRef;

                if (latitude == null || latitude.isEmpty()){
                    if (longitude == null || longitude.isEmpty()){
                        // lat and long == 0, then match all as true
                        geoBool = true;
                    }
                    else {
                        // lat = 0 and long != 0, then only match lat
                        longitudeBool = convert(Double.parseDouble(longitude)).equals(exifLongitude);
                        longitudeBoolRef = longitudeRef(Double.parseDouble(longitude)).equals(exifLongitude_ref);
                        geoBool = longitudeBool && longitudeBoolRef;
                    }
                }
                else {
                    if (longitude == null || longitude.isEmpty()){
                        // lat != 0 and long == 0, then only match lat
                        latitudeBool = convert(Double.parseDouble(latitude)).equals(exifLatitude);
                        latitudeBoolRef = latitudeRef(Double.parseDouble(latitude)).equals(exifLatitude_ref);
                        geoBool = latitudeBool && latitudeBoolRef;
                    }
                    else{
                        // lat != 0 and long != 0, then match lat & long
                        latitudeBool = convert(Double.parseDouble(latitude)).equals(exifLatitude);
                        latitudeBoolRef = latitudeRef(Double.parseDouble(latitude)).equals(exifLatitude_ref);
                        longitudeBool = convert(Double.parseDouble(longitude)).equals(exifLongitude);
                        longitudeBoolRef = longitudeRef(Double.parseDouble(longitude)).equals(exifLongitude_ref);
                        geoBool = latitudeBool && latitudeBoolRef && longitudeBool && longitudeBoolRef;
                    }
                }

                if (
                        ((startTimestamp == null && endTimestamp == null) || (f.lastModified() >= startTimestamp.getTime() && f.lastModified() <= endTimestamp.getTime()))
                                && (geoBool)
                                && (keywords == "" || f.getPath().contains(keywords))
                ) {
                    photos.add(f.getPath());
                }
            }
        }
        return photos;
    }



    public void scrollPhotos(View v) {
        String newFileName = updatePhoto(photos.get(index), ((EditText) findViewById(R.id.etCaption)).getText().toString());
        photos.set(index, newFileName);

        switch (v.getId()) {
            case R.id.btnPrev:
                if (index > 0) {
                    index--;
                }
                break;
            case R.id.btnNext:
                if (index < (photos.size() - 1)) {
                index++;
            }
            break;
            default:
                break;
        }
        displayPhoto(photos.get(index));
    }

    private void displayPhoto(String path) {
        ImageView iv = (ImageView) findViewById(R.id.ivGallery);
        TextView tv = (TextView) findViewById(R.id.tvTimestamp);
        EditText et = (EditText) findViewById(R.id.etCaption);
        if (path == null || path =="") {
            iv.setImageResource(R.mipmap.ic_launcher);
            et.setText("");
            tv.setText("");
        } else {
            iv.setImageBitmap(BitmapFactory.decodeFile(path));
            String[] attr = path.split("_");
            et.setText(attr[1]);
            tv.setText(attr[2]);
        }
    }

    private String updatePhoto(String path, String caption) {
        String[] attr = path.split("_");
        if (attr.length >= 3) {
            String pathname = attr[0] + "_" + caption + "_" + attr[2] + "_" + attr[3];
            File to = new File(pathname);
            File from = new File(path);
            from.renameTo(to);
            return pathname;
        }
        return path;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "_caption_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                DateFormat format = new SimpleDateFormat("yyyy‐MM‐dd HH:mm:ss");
                Date startTimestamp , endTimestamp;
                try {
                    String from = (String) data.getStringExtra("STARTTIMESTAMP");
                    String to = (String) data.getStringExtra("ENDTIMESTAMP");
                    startTimestamp = format.parse(from);
                    endTimestamp = format.parse(to);
                } catch (Exception ex) {
                    startTimestamp = null;
                    endTimestamp = null;
                }
                String latitude = (String) data.getStringExtra("LATITUDE");
                String longitude = (String) data.getStringExtra("LONGITUDE");
                String keywords = (String) data.getStringExtra("KEYWORDS");
                index = 0;
                photos = findPhotos(startTimestamp, endTimestamp, latitude, longitude, keywords);
                if (photos.size() == 0) {
                    displayPhoto(null);
                } else {
                    displayPhoto(photos.get(index));
                }
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ImageView mImageView = (ImageView) findViewById(R.id.ivGallery);
            mImageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
            setExif(mCurrentPhotoPath);
            photos = findPhotos(new Date(Long.MIN_VALUE), new Date(), null, null, "");
        }
    }
    public void openSearch(View v) {
        Intent searchIntent = new Intent(this, SearchActivity.class);
        startActivityForResult(searchIntent, SEARCH_ACTIVITY_REQUEST_CODE);
    }
}