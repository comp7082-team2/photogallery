package com.example.photogallery.Utils;

import android.app.Activity;

import java.io.File;
import java.io.IOException;

public class ProxyFileHelper implements IFileHelper{
    private final IFileHelper service = new FileHelper();
    @Override
    public File createFile(Activity context) throws IOException {
        return service.createFile(context);
    }
}
