package com.example.photogallery.Presenters;

import static android.app.Activity.RESULT_OK;

import static com.example.photogallery.Utils.ExifHelper.convert;
import static com.example.photogallery.Utils.ExifHelper.latitudeRef;
import static com.example.photogallery.Utils.ExifHelper.longitudeRef;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.example.photogallery.Models.IPhotoRepository;
import com.example.photogallery.Models.Photo;
import com.example.photogallery.Models.PhotoRepository;
import com.example.photogallery.Views.MainActivity;
import com.example.photogallery.Views.SearchActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GalleryPresenter {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SEARCH_ACTIVITY_REQUEST_CODE = 2;
    private static final String AUTHORITY = "com.example.photogallery.fileprovider";
    private static final String IMAGE_TYPE_JPEG = "image/jpg";
    private static final String TAG = GalleryPresenter.class.getName();

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private double latitude = 0.0;
    private double longitude = 0.0;

    private IPhotoRepository photoRepository;
    private Activity context;
    private List<Photo> photos;
    private int index = 0;

    public GalleryPresenter(Activity context) {
        this.context = context;
        photoRepository = new PhotoRepository();
        photos = photoRepository.findPhotos(null, null, null, null, null);
        displayPhoto();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

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

    public void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    public void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    public void setExif(String path) {
        fusedLocationClient.getLastLocation().addOnSuccessListener(context, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    try {
                        ExifInterface exif = new ExifInterface(path);
                        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, latitudeRef(latitude));
                        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, longitudeRef(longitude));
                        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, convert(latitude));
                        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, convert(longitude));
                        exif.saveAttributes();
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            }
        });
    }

    public void takePhoto(android.view.View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;

        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(context, AUTHORITY, photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            context.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void openSearch() {
        Intent searchIntent = new Intent(context, SearchActivity.class);
        context.startActivityForResult(searchIntent, SEARCH_ACTIVITY_REQUEST_CODE);
    }

    private void displayPhoto() {
        if (photos.size() > 0) {
            Photo photo = photos.get(index);
            ((MainActivity) context).displayPhoto(photo.getBitmap(),
                    photo.getCaption(),
                    photo.getDatestamp(),
                    index == 0,
                    index == photos.size() - 1);
        } else {
            // Display nothing
            ((MainActivity) context).displayPhoto(null,
                    null,
                    null,
                    true,
                    true);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "__" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        photoRepository.save(new Photo(image));

        return image;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                DateFormat format = new SimpleDateFormat("yyyy‐MM‐dd HH:mm:ss");
                Date startTimestamp = null,
                        endTimestamp = null;
                index = 0;
                try {
                    String from = data.getStringExtra("STARTTIMESTAMP");
                    String to = data.getStringExtra("ENDTIMESTAMP");
                    startTimestamp = format.parse(from);
                    endTimestamp = format.parse(to);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                photos = photoRepository.findPhotos(startTimestamp,
                        endTimestamp,
                        data.getStringExtra("KEYWORDS"),
                        data.getStringExtra("LATITUDE"),
                        data.getStringExtra("LONGITUDE"));
                displayPhoto();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            photos = photoRepository.findPhotos(null, null, null, null, null);
            index = photos.size() - 1;
            setExif(photos.get(index).getPhotoFile().getPath());
            displayPhoto();
        }
    }

    public void sharePhoto() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType(IMAGE_TYPE_JPEG);
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse(photos.get(index).getPhotoFile().getPath()));
        context.startActivity(Intent.createChooser(share, "Share Image"));
    }

    public void scrollPhotos(boolean isNext, String caption) {
        if (caption != null) {
            Photo photo = photos.get(index);
            photo.updateCaption(caption);
            photos.set(index, photo);
        }
        if (isNext) {
            index++;
        } else {
            index--;
        }
        displayPhoto();
    }

    public interface View {
        void displayPhoto(Bitmap bitmap, String caption, String datestamp, boolean isFirst, boolean isLast);
    }
}
