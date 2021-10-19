package com.example.photogallery.Utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import android.util.Log;

@Aspect
public class Logging {
    // Models/PhotoRepository.java
    @Before("execution(* *.findPhotos(..))")
    public void findPhotos1(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        Date startTimestamp = (Date) args[0];
        Date endTimestamp = (Date) args[1];
        String keywords = (String) args[2];
        String latitude = (String) args[3];
        String longitude = (String) args[4];

        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        builderString.append("\nSearch Parameters\nStart Time: ");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        if (startTimestamp != null){builderString.append(dateFormat.format(startTimestamp));}
        else{builderString.append("null");}

        builderString.append("\nEnd Time: ");
        if (endTimestamp != null){builderString.append(dateFormat.format(endTimestamp));}
        else{builderString.append("null");}

        builderString.append("\nSearch Keyword(s): ");
        builderString.append(keywords);
        builderString.append("\nLatitude: ");
        builderString.append(latitude);
        builderString.append("\nLongitude: ");
        builderString.append(longitude);
        Log.d("Start Call: ", builderString.toString());
    }

    @After("execution(* *.findPhotos(..))")
    public void findPhotos2(JoinPoint joinPoint) {
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        Log.d("Finish Call: ", builderString.toString());
    }

    @Before("execution(* *.loadPhotos(..))")
    public void loadPhotos1(JoinPoint joinPoint) {
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());
        
        Log.d("Start Call: ", builderString.toString());
    }

    @After("execution(* *.loadPhotos(..))")
    public void loadPhotos2(JoinPoint joinPoint){
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        Log.d("Finish Call: ", builderString.toString());
    }

    @Before("execution (* *.save(..))")
    public void save1(JoinPoint joinPoint){

        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        Log.d("Start Call: ", builderString.toString());
    }

    @After("execution(* *.save(..))")
    public void save2(JoinPoint joinPoint){
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        Log.d("Finish Call: ", builderString.toString());
    }

    // Models/Photo.java
    @Before("execution(* *.getPhotoFile(..))")
    public void getPhotoFile1(JoinPoint joinPoint) {
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());
        
        Log.d("Start Call: ", builderString.toString());
    }
    
    @After("execution(* *.getPhotoFile(..))")
    public void getPhotoFile2(JoinPoint joinPoint, File photoFile){
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        Log.d("Finish Call: ", builderString.toString());
    }

    @Before("execution(* *.getCaption(..))")
    public void getCaption1(JoinPoint joinPoint) {
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        Log.d("Start Call: ", builderString.toString());
    }

    @AfterReturning(pointcut="execution(* *.getCaption(..))", returning="caption")
    public void getCaption2(JoinPoint joinPoint, String caption) {
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        builderString.append("\nReturn Caption: ");
        builderString.append(caption);

        Log.d("Finish Call:", builderString.toString());
    }

    @Before("execution(* *.updateCaption(..))")
    public void updateCaption1(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String caption = (String) args[0];

        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());
        builderString.append("\nNew Caption: ");
        builderString.append(caption);

        Log.d("Start Call: ", builderString.toString());
    }

    @After("execution(* *.updateCaption(..))")
    public void updateCaption2(JoinPoint joinPoint){
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        Log.d("Finish Call: ", builderString.toString());
    }

    @Before("execution(* *.getDatestamp(..))")
    public void getDatestamp1(JoinPoint joinPoint) {
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        Log.d("Start Call: ", builderString.toString());
    }   

    @AfterReturning(pointcut="execution(* *.getDatestamp(..))", returning="dateString")
    public void getDatestamp2(JoinPoint joinPoint, String dateString) {
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        builderString.append("\nReturn Date Stamp: ");
        builderString.append(dateString);

        Log.d("Finish Call:", builderString.toString());
    }

    @Before("execution(* *.getBitmap(..))")
    public void getBitmap1(JoinPoint joinPoint) {
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        Log.d("Start Call: ", builderString.toString());
    }

    @After("execution(* *.getBitmap(..))")
    public void getDatestamp2(JoinPoint joinPoint) {
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        Log.d("Finish Call:", builderString.toString());
    }

    @Before("execution(* *.getDate(..))")
    public void getDate1(JoinPoint joinPoint) {
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        Log.d("Start Call: ", builderString.toString());
    }

    @AfterReturning(pointcut="execution(* *.getDate(..))", returning="date")
    public void getDate2(JoinPoint joinPoint, Date date) {        
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        builderString.append("\nReturn Date: ");
        if (date==null){builderString.append("null");}
        else{builderString.append(date);}

        Log.d("Finish Call:", builderString.toString());
    }

    // Utils/ExifHelper.java
    @Before("execution(* *.convert(..))")
    public void convert1(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        double lat_long = (double) args[0];
        
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());
        builderString.append("\nDecimal to DMS Conversion Parameters (No Reference)\nLatitude/Longitude in Decimal:");
        builderString.append(lat_long);

        Log.d("Start Call: ", builderString.toString());
    }

    @AfterReturning(pointcut="execution(* *.convert(..))", returning="DMS")
    public void convert2(JoinPoint joinPoint, String DMS) {        
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        builderString.append("\nReturn Latitude/Longitude in DMS (No Reference): ");
        builderString.append(DMS);

        Log.d("Finish Call:", builderString.toString());
    }

    @Before("execution(* *.latitudeRef(..))")
    public void latitudeRef1(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        double latitude = (double) args[0];
        
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());
        builderString.append("\nLatitude Reference Converter Parameters\nLatitude:");
        builderString.append(latitude);

        Log.d("Start Call: ", builderString.toString());
    }

    @AfterReturning(pointcut="execution(* *.latitudeRef(..))", returning="latitudeRef")
    public void latitudeRef2(JoinPoint joinPoint, String latitudeRef) {        
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        builderString.append("\nReturn Latitude Reference: ");
        builderString.append(latitudeRef);

        Log.d("Finish Call:", builderString.toString());
    }

    @Before("execution(* *.longitudeRef(..))")
    public void longitudeRef1(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        double longitude = (double) args[0];
        
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());
        builderString.append("\nLongitude Reference Converter Parameters\nLongitude:");
        builderString.append(longitude);

        Log.d("Start Call: ", builderString.toString());
    }

    @AfterReturning(pointcut="execution(* *.longitudeRef(..))", returning="longitudeRef")
    public void longitudeRef2(JoinPoint joinPoint, String longitudeRef) {        
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        builderString.append("\nReturn Longitude Reference: ");
        builderString.append(longitudeRef);

        Log.d("Finish Call:", builderString.toString());
    }
    
    // GalleryPresenter.java
    @Before("execution(* *.startLocationUpdates(..))")
    public void startLocationUpdates1(JoinPoint joinPoint) {
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        Log.d("Start Call: ", builderString.toString());
    }

    @After("execution(* *.startLocationUpdates(..))")
    public void startLocationUpdates2(JoinPoint joinPoint){
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        Log.d("Finish Call: ", builderString.toString());
    }

    @Before("execution(* *.stopLocationUpdates(..))")
    public void stopLocationUpdates1(JoinPoint joinPoint) {
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        Log.d("Start Call: ", builderString.toString());
    }

    @After("execution(* *.stopLocationUpdates(..))")
    public void stopLocationUpdates2(JoinPoint joinPoint){
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        Log.d("Finish Call: ", builderString.toString());
    }

    @Before("execution(* *.setExif(..))")
    public void setExif1(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String path = (String) args[0];

        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());
        builderString.append("\nSetting EXIF at: ");
        builderString.append(path);

        Log.d("Start Call: ", builderString.toString());
    }

    @After("execution(* *.setExif(..))")
    public void setExif2(JoinPoint joinPoint){
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        Log.d("Finish Call: ", builderString.toString());
    }

    @Before("execution(* *.takePhoto(..))")
    public void takePhoto1(JoinPoint joinPoint) {
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        Log.d("Start Call: ", builderString.toString());
    }

    @After("execution(* *.takePhoto(..))")
    public void takePhoto2(JoinPoint joinPoint){
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        Log.d("Finish Call: ", builderString.toString());
    }    

    @Before("execution(* *.openSearch(..))")
    public void openSearch1(JoinPoint joinPoint) {
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        Log.d("Start Call: ", builderString.toString());
    }

    @After("execution(* *.openSearch(..))")
    public void openSearch2(JoinPoint joinPoint){
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        Log.d("Finish Call: ", builderString.toString());
    }

    @Before("execution(* com.example.photogallery.Presenters.GalleryPresenter.displayPhoto(..))")
    public void displayPhoto1(JoinPoint joinPoint) {
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        Log.d("Start Call: ", builderString.toString());
    }

    @After("execution(* *.displayPhoto(..))")
    public void displayPhoto2(JoinPoint joinPoint){
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        Log.d("Finish Call: ", builderString.toString());
    }

    @Before("execution(* *.createImageFile(..))")
    public void createImageFile1(JoinPoint joinPoint) {
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        Log.d("Start Call: ", builderString.toString());
    }

    @After("execution(* com.example.photogallery.Presenters.GalleryPresenter.createImageFile(..))")
    public void createImageFile2(JoinPoint joinPoint){
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        Log.d("Finish Call: ", builderString.toString());
    }

    @Before("execution(* com.example.photogallery.Presenters.GalleryPresenter.onActivityResult(..))")
    public void onActivityResult1(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        int requestCode = (int) args[0];
        int resultCode = (int) args[1];

        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());
        builderString.append("\nrequestCode: ");
        builderString.append(requestCode);
        builderString.append("\nresultCode: ");
        builderString.append(resultCode);
        
        Log.d("Start Call: ", builderString.toString());
    }

    @After("execution(* *.onActivityResult(..))")
    public void onActivityResult2(JoinPoint joinPoint){
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        Log.d("Finish Call: ", builderString.toString());
    } 
    
    @Before("execution(* *.sharePhoto(..))")
    public void sharePhoto1(JoinPoint joinPoint) {
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        Log.d("Start Call: ", builderString.toString());
    }

    @After("execution(* *.sharePhoto(..))")
    public void sharePhoto2(JoinPoint joinPoint){
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        Log.d("Finish Call: ", builderString.toString());
    }
    
    @Before("execution(* com.example.photogallery.Presenters.GalleryPresenter.scrollPhotos(..))")
    public void scrollPhotos1(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        boolean isNext = (boolean) args[0];
        String caption = (String) args[1];

        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());
        builderString.append("\nisNext: ");
        builderString.append(isNext);
        builderString.append("\ncaption: ");
        builderString.append(caption);
        
        Log.d("Start Call: ", builderString.toString());
    }

    @After("execution(* com.example.photogallery.Presenters.GalleryPresenter.scrollPhotos(..))")
    public void scrollPhotos2(JoinPoint joinPoint){
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        Log.d("Finish Call: ", builderString.toString());
    }

    // SearchActivity.java
    @Before("execution(* *.onCreate(..))")
    public void onCreate1(JoinPoint joinPoint) {
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());
        
        Log.d("Start Call: ", builderString.toString());
    }

    @After("execution(* *.onCreate(..))")
    public void onCreate2(JoinPoint joinPoint){
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        Log.d("Finish Call: ", builderString.toString());
    }

    @Before("execution(* *.cancel(..))")
    public void cancel1(JoinPoint joinPoint) {
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());
        
        Log.d("Start Call: ", builderString.toString());
    }

    @After("execution(* *.cancel(..))")
    public void cancel2(JoinPoint joinPoint){
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        Log.d("Finish Call: ", builderString.toString());
    }

    @Before("execution(* *.go(..))")
    public void go1(JoinPoint joinPoint) {
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());
        
        Log.d("Start Call: ", builderString.toString());
    }

    @After("execution(* *.go(..))")
    public void go2(JoinPoint joinPoint){
        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());

        Log.d("Finish Call: ", builderString.toString());
    }

    // MainActivity
    @Before("execution(* com.example.photogallery.Views.MainActivity.onActivityResult(..))")
    public void onActivityResultMA1(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();

        int requestCode = (int) args[0];
        int resultCode = (int) args[1];
        String intent = "null";

        if(args[2] != null){intent = args[2].toString();}

        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());
        builderString.append("\nonActivityResult Parameters\nRequest Code: ");
        builderString.append(requestCode);
        builderString.append("\nResult Code: ");
        builderString.append(resultCode);
        builderString.append("\nIntent: ");
        builderString.append(intent);

        Log.d("Start Call: ", builderString.toString());
    }

    @Before("execution(* com.example.photogallery.Views.MainActivity.displayPhoto(..))")
    public void displayPhotoMA1(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();

        String caption = (String) args[1];
        String datestamp = (String) args[2];
        boolean isFirst = (boolean) args[3];
        boolean isLast = (boolean) args[4];

        StringBuilder builderString = new StringBuilder();
        builderString.append(joinPoint.toLongString());
        builderString.append("\nDisplay Photo Main Activity Parameters\nCaption: ");
        builderString.append(caption);
        builderString.append("\nDate Stamp: ");
        builderString.append(datestamp);
        builderString.append("\nPrevious Button Flag: ");
        builderString.append(isFirst);
        builderString.append("\nNext Button Flag: ");
        builderString.append(isLast);
        
        Log.d("Start Call: ", builderString.toString());
    }
}
