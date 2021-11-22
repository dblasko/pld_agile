package com.pld.agile.view;

import com.pld.agile.Observable;
import com.pld.agile.Observer;
import com.pld.agile.model.map.MapData;
import com.pld.agile.model.tour.TourData;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class GraphicalView implements Observer {

    private GraphicalViewMap graphicalViewMap;
    private GraphicalViewRequests graphicalViewRequests;
    private Pane component;

    private MapData mapData;
    private TourData tourData;

    public GraphicalView(Scene parent) {

        // Add observers
        this.mapData = MapData.getInstance();
        mapData.addObserver(this);
        this.tourData = TourData.getInstance();
        tourData.addObserver(this);

        graphicalViewMap = new GraphicalViewMap(mapData, parent);
        graphicalViewRequests = new GraphicalViewRequests(mapData, tourData, graphicalViewMap);

        component = new Pane();
        component.getChildren().addAll(graphicalViewMap, graphicalViewRequests);

    }

    @Override
    public void update(Observable o, Object arg) {
        graphicalViewMap.draw();
        graphicalViewRequests.draw();
    }

    public Node getComponent() {
        return component;
    }

}
