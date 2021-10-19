package com.example.photogallery.Models;

import java.util.Date;
import java.util.List;

public interface IPhotoRepository {
    List<Photo> findPhotos(Date startTimestamp, Date endTimestamp, String keywords, String latitude, String longitude) ;
    void save(Photo photo);
}
