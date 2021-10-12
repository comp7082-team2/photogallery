package com.example.photogallery.Views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.photogallery.Presenters.GalleryPresenter;
import com.example.photogallery.R;

public class MainActivity extends AppCompatActivity implements GalleryPresenter.View {

    private GalleryPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new GalleryPresenter(this);
    }

    protected void onResume() {
        super.onResume();
        presenter.startLocationUpdates();
    }

    protected void onPause() {
        super.onPause();
        presenter.stopLocationUpdates();
    }

    public void takePhoto(View v) {
        presenter.takePhoto(v);
    }

    public void scrollPhotos(View v) {
        presenter.scrollPhotos(v.getId() == R.id.btnNext,
                ((EditText) findViewById(R.id.etCaption)).getText().toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, resultCode, data);
    }
    public void openSearch(View v) {
        presenter.openSearch();
    }

    public void sharePhoto(View v) {
        presenter.sharePhoto();
    }

    @Override
    public void displayPhoto(Bitmap bitmap, String caption, String datestamp, boolean isFirst, boolean isLast) {
        ImageView iv = (ImageView) findViewById(R.id.ivGallery);
        TextView tv = (TextView) findViewById(R.id.tvTimestamp);
        EditText et = (EditText) findViewById(R.id.etCaption);
        Button btnNext = (Button) findViewById(R.id.btnNext);
        Button btnPrev = (Button) findViewById(R.id.btnPrev);
        if (bitmap == null) {
            iv.setImageResource(R.mipmap.ic_launcher);
            et.setText("");
            tv.setText("");
        } else {
            iv.setImageBitmap(bitmap);
            et.setText(caption);
            tv.setText(datestamp);
        }
        btnPrev.setEnabled(!isFirst);
        btnNext.setEnabled(!isLast);
    }
}