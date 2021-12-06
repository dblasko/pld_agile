/*
 * TourDataTests
 *
 * Copyright (c) 2021. Hexanomnom
 */

package com.pld.agile.model.tour;

import com.pld.agile.controller.Controller;
import com.pld.agile.controller.LoadedRequestsState;
import com.pld.agile.model.map.Intersection;
import com.pld.agile.model.map.MapData;
import com.pld.agile.utils.exception.PathException;
import com.pld.agile.utils.exception.SyntaxException;
import com.pld.agile.utils.parsing.MapLoader;
import com.pld.agile.utils.parsing.RequestLoader;

import com.pld.agile.utils.tsp.CompleteGraph;
import com.pld.agile.utils.tsp.Graph;
import com.pld.agile.utils.tsp.TSP3;
import com.pld.agile.view.Window;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class TourDataTest {
    private final MapData mapData = new MapData ();
    private final MapLoader mapLoader = new MapLoader("test/resources/loadMap_loadRequestsBase.xml", mapData);
    private RequestLoader requestLoader;
    private TourData tourData = new TourData();

    /*@BeforeEach
    public void loadMap (){
        try {
            mapLoader.load();
            tourData.setAssociatedMap(mapData);
            requestLoader = new RequestLoader("test/resources/computeTour_notOptimalTour.xml", tourData);
            requestLoader.load();
            //compute tour
            List<Path> paths = new ArrayList<>();
            paths.add(new Path(tourData.getWarehouse(),tourData.getRequestList().get(1).getPickup()));
            paths.add(new Path(tourData.getRequestList().get(1).getPickup(),tourData.getRequestList().get(1).getDelivery()));
            paths.add(new Path(tourData.getRequestList().get(1).getDelivery(),tourData.getRequestList().get(2).getPickup()));
            paths.add(new Path(tourData.getRequestList().get(2).getPickup(),tourData.getRequestList().get(2).getDelivery()));
            paths.add(new Path(tourData.getRequestList().get(2).getDelivery(),tourData.getWarehouse()));
            tourData.setTourPaths(paths);

            List<Integer> stops = new ArrayList<>();
            stops.add(0);
            stops.add(0);
            stops.add(4);
            stops.add(1);
            stops.add(3);
            stops.add(2);
            tourData.setStops(stops);
            HashMap<Integer,Stop> stopMap = new HashMap<>() {{
                put(tourData.getWarehouse().getAddress().getId(), tourData.getWarehouse());
                put(tourData.getRequestList().get(0).getDelivery().getAddress().getId(),tourData.getRequestList().get(0).getDelivery());
                put(tourData.getRequestList().get(1).getPickup().getAddress().getId(),tourData.getRequestList().get(1).getPickup());
                put(tourData.getRequestList().get(2).getPickup().getAddress().getId(),tourData.getRequestList().get(2).getPickup());
                put(tourData.getRequestList().get(2).getDelivery().getAddress().getId(),tourData.getRequestList().get(2).getDelivery());
            }};
            tourData.setStopMap(stopMap);
            Graph stopsGraph = new CompleteGraph(stops.size());
            for (int i = 0; i<stops.size();i++) {
                for (int j = 0; j<tourData.getStops().size(); j++){
                    if(i!=j){
                        Path path = new Path(tourData.getStopMap().get(i),tourData.getStopMap().get(j));
                        stopsGraph.setPath(i, j,path);
                        stopsGraph.setCost(i, j, path.getLength());
                    }
                }
            }
            tourData.setStopsGraph(stopsGraph);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testTourData(){
        TourData tourData = new TourData();
        assertEquals(new ArrayList<>(),tourData.getRequestList());
        assertEquals(new ArrayList<>(),tourData.getTourPaths());
        assertNull(tourData.getAssociatedMap());
        assertNull(tourData.getDepartureTime());
        assertNull(tourData.getWarehouse());
    }
    @Test
    public void testDeleteRequest (){
        tourData.deleteRequest(tourData.getRequestList().get(1));
        assertEquals(2,tourData.getRequestList().size());
    }
    @Test
    public void testAddRequest () {
        Intersection pickupAddress = mapData.getIntersections().get(3);
        Intersection deliveryAddress = mapData.getIntersections().get(4);
        Request newRequest = new Request ();
        Stop pickup = new Stop (newRequest,pickupAddress,0,StopType.PICKUP);
        Stop delivery = new Stop (newRequest,deliveryAddress,0,StopType.DELIVERY);
        newRequest.setDelivery(delivery);
        newRequest.setPickup(pickup);

        tourData.getRequestList().add(newRequest);
        try {
            tourData.addRequest(newRequest);
        } catch (Exception e) {}

        //stops
        assertEquals(newRequest.getDelivery().getAddress().getId(),tourData.getStops().get(tourData.getStops().size()-1));
        //stopsMap
        assertEquals(newRequest.getDelivery(),tourData.getStopMap().get(newRequest.getDelivery().getAddress().getId()));
    }
    @Test
    public void testConstructNewRequest(){
        Intersection pickupAddress = mapData.getIntersections().get(3);
        Intersection deliveryAddress = mapData.getIntersections().get(4);
        Request newRequest = new Request ();
        Stop pickup = new Stop (newRequest,pickupAddress,0,StopType.PICKUP);
        Stop delivery = new Stop (newRequest,deliveryAddress,0,StopType.DELIVERY);
        newRequest.setDelivery(delivery);
        newRequest.setPickup(pickup);

        tourData.constructNewRequest1(pickupAddress);
        try {
            tourData.constructNewRequest2(deliveryAddress);
        } catch (PathException e) {}

        assertEquals(newRequest.toString(),tourData.getRequestList().get(tourData.getRequestList().size()-1).toString());

    }

    @Test
    public void testStopIsShiftable() {
        Stop stop = tourData.getTourPaths().get(0).getDestination();
        assertTrue(tourData.stopIsShiftable(stop,2));
        assertFalse(tourData.stopIsShiftable(stop,-2));
    }

    @Test
    public void testShiftStopOrder() {
        Stop stop = tourData.getTourPaths().get(0).getDestination();
        assertFalse(tourData.shiftStopOrder(stop,-2));
        assertTrue(tourData.shiftStopOrder(stop,2));

    }

   @Test
    public void testStopComputingTour() {
        TourData tourDataEmpty = new TourData();
       assertFalse(tourDataEmpty.stopComputingTour());
       tourData.setTourComputingThread(new Thread());
       assertTrue(tourData.stopComputingTour());
   }

   @Test
   public void testComputeTour(){

   }

   @Test
    public void testProcessTSPUpdate(){
        TourData unInitTourData = new TourData();
       unInitTourData.setAssociatedMap(mapData);
       RequestLoader requestLoader2 = new RequestLoader("test/resources/computeTour_notOptimalTour.xml", unInitTourData);
       try {
           requestLoader2.load();
       } catch (SyntaxException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }

       List<Integer> stops = new ArrayList<>();
       stops.add(0);
       stops.add(0);
       stops.add(4);
       stops.add(1);
       stops.add(3);
       stops.add(2);
       unInitTourData.setStops(stops);
       HashMap<Integer,Stop> stopMap = new HashMap<>() {{
           put(unInitTourData.getWarehouse().getAddress().getId(), unInitTourData.getWarehouse());
           put(unInitTourData.getRequestList().get(0).getDelivery().getAddress().getId(),unInitTourData.getRequestList().get(0).getDelivery());
           put(unInitTourData.getRequestList().get(1).getPickup().getAddress().getId(),unInitTourData.getRequestList().get(1).getPickup());
           put(unInitTourData.getRequestList().get(2).getPickup().getAddress().getId(),unInitTourData.getRequestList().get(2).getPickup());
           put(unInitTourData.getRequestList().get(2).getDelivery().getAddress().getId(),unInitTourData.getRequestList().get(2).getDelivery());
       }};
       unInitTourData.setStopMap(stopMap);
       Graph stopsGraph = new CompleteGraph(stops.size());
       for (int i = 0; i<stops.size();i++) {
           for (int j = 0; j<unInitTourData.getStops().size(); j++){
               if(i!=j){
                   Path path = new Path(unInitTourData.getStopMap().get(i),unInitTourData.getStopMap().get(j));
                   stopsGraph.setPath(i, j,path);
                   stopsGraph.setCost(i, j, path.getLength());
               }
           }
       }
       System.out.println();
       unInitTourData.setStopsGraph(stopsGraph);
        unInitTourData.processTSPUpdate(new TSP3(unInitTourData));
        assertNotNull(unInitTourData.getTourPaths());
   }

*/


}