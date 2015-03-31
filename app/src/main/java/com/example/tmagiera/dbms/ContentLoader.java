package com.example.tmagiera.dbms;

import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by tmagiera on 2015-03-31.
 */
public class ContentLoader {
    private static Map<String, ContentEntity> contentEntityMap = new ArrayMap<>();
    private static List<ContentEntity> contentEntityList = new ArrayList<>();

    ContentLoader() {}

    ContentLoader(final List<ContentEntity> contentEntities) {
        contentEntityMap.clear();
        for (ContentEntity entity : contentEntities) {
            contentEntityMap.put(entity.getCode(), entity);
        }

        contentEntityList = contentEntities;
    }

    public static ContentEntity getItem(String code) {
        return contentEntityMap.get(code);
    }

    public static List<ContentEntity> getItems() {
        return contentEntityList;
    }
}
