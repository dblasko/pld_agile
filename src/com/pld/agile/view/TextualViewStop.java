package com.pld.agile.view;

import com.pld.agile.model.tour.Stop;
import com.pld.agile.model.tour.StopType;
import com.pld.agile.utils.view.ViewUtilities;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

public class TextualViewStop extends VBox {

    public TextualViewStop(Stop stop) {

        double lat = stop.getAddress().getLatitude();
        double lon = stop.getAddress().getLongitude();
        double duration = stop.getDuration();
        StopType type = stop.getType();
        String hour = ""; //stop.getHour();
        Color colour = Color.BLACK;
        if (type != StopType.WAREHOUSE) {
            colour = ViewUtilities.stringToColour(stop.getRequest().getPickup().toString());
        }

        VBox panel = new VBox(8);
        HBox labelPanel = new HBox(8);
        GridPane infoPane = new GridPane();
        infoPane.setHgap(4);
        infoPane.setVgap(4);

        // Label
        VBox labelGraphic = new GraphicalViewStop(stop, 16);
        String labelTextString = "";
        switch (type) {
            case PICKUP -> labelTextString = "Pickup Stop";
            case DELIVERY -> labelTextString = "Delivery Stop";
            case WAREHOUSE -> labelTextString = "Warehouse";
        }
        Text labelText = new Text(labelTextString);
        labelText.getStyleClass().add("textual-view-stop-panel-label");
        labelPanel.setAlignment(Pos.CENTER_LEFT);
        labelPanel.getChildren().addAll(labelGraphic, labelText);

        // Latitude
        Text latText = new Text("Latitude:");
        TextField latInput = new TextField(lat + "");
        latInput.setEditable(false);
        latInput.setMouseTransparent(true);
        latInput.setFocusTraversable(false);
        latInput.setPrefWidth(90);
        infoPane.add(latText, 0, 0);
        infoPane.add(latInput, 1, 0);

        // Longitude
        Text lonText = new Text(" Longitude:");
        TextField lonInput = new TextField(lon + "");
        lonInput.setEditable(false);
        lonInput.setMouseTransparent(true);
        lonInput.setFocusTraversable(false);
        lonInput.setPrefWidth(90);
        infoPane.add(lonText, 2, 0);
        infoPane.add(lonInput, 3, 0);

        // Hour of passage
        Text hourText = new Text("Time:");
        TextField hourInput = new TextField(hour);
        hourInput.setEditable(false);
        hourInput.setMouseTransparent(true);
        hourInput.setFocusTraversable(false);
        hourInput.setPrefWidth(90);
        infoPane.add(hourText, 0, 1);
        infoPane.add(hourInput, 1, 1);

        // Duration
        if (type != StopType.WAREHOUSE) {
            Text durationText = new Text(" Duration:");
            TextField durationInput = new TextField(duration + "");
            durationInput.setEditable(false);
            durationInput.setMouseTransparent(true);
            durationInput.setFocusTraversable(false);
            durationInput.setPrefWidth(90);
            infoPane.add(durationText, 2, 1);
            infoPane.add(durationInput, 3, 1);
        }

        // Add it all together
        panel.getChildren().addAll(labelPanel, infoPane);
        this.getChildren().add(panel);

        this.setPadding(new Insets(10));
        this.setSpacing(10);
        this.getStyleClass().add("textual-view-stop-panel");

    }


}