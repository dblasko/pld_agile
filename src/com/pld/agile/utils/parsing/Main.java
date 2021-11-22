package com.pld.agile.utils.parsing;

import com.pld.agile.model.MapData;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        String filePath = "src/resources/fichiersXML2020/smallMap.xml";
        MapData mapData = MapData.getInstance();
        MapLoader mapLoader = new MapLoader(filePath, mapData);
        boolean success = mapLoader.load();

        System.out.println("SUCCESS : " + success);
        System.out.println(mapData);

    }
}