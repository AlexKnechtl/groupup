package com.example.alexander.groupup.Models;

import java.util.HashMap;
import java.util.Random;

public class GroupImagesModel {

    // Hashmap for Images: <Activity, URLs>
    private static HashMap<String, String[]> imageUrlMap = new HashMap<>();

    static {
        imageUrlMap.put("Tennis", new String[]{"https://firebasestorage.googleapis.com/v0/b/groupup-e980d.appspot.com/o/group_images%2Fsport_tennis_1.png?alt=media&token=3d4ecb66-7274-4984-be50-a17b42d0b873"});
        imageUrlMap.put("Biking", new String[]{"https://firebasestorage.googleapis.com/v0/b/groupup-e980d.appspot.com/o/group_images%2Fsport_bike_1.png?alt=media&token=39b0b532-ad2f-4f3e-97ea-b05ad2be54b2"});
        imageUrlMap.put("Running", new String[]{"https://firebasestorage.googleapis.com/v0/b/groupup-e980d.appspot.com/o/group_images%2Fsport_bike_1.png?alt=media&token=39b0b532-ad2f-4f3e-97ea-b05ad2be54b2"});

    }

    public static String getRandomImageURL(String activity)
    {
        String[] images;
        try {
            images = imageUrlMap.get(activity);
        }
        catch (Exception e)
        {
            images = new String[]{"https://firebasestorage.googleapis.com/v0/b/groupup-e980d.appspot.com/o/group_images%2Fsport_bike_1.png?alt=media&token=39b0b532-ad2f-4f3e-97ea-b05ad2be54b2"};
        }
        Random rnd = new Random();
        return images[rnd.nextInt(images.length)];
    }
}