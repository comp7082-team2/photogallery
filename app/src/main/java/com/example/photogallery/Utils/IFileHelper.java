package com.example.photogallery.Utils;

import android.app.Activity;

import java.io.File;
import java.io.IOException;

public interface IFileHelper {
    File createFile(Activity context) throws IOException, IOException;
}
