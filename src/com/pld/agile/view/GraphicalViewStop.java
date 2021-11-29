/*
 * GraphicalViewStop
 *
 * Copyright (c) 2021. Hexanomnom
 */

package com.pld.agile.view;

import com.pld.agile.model.tour.Stop;
import com.pld.agile.utils.observer.Observable;
import com.pld.agile.utils.observer.Observer;
import com.pld.agile.utils.observer.UpdateType;
import com.pld.agile.utils.view.ViewUtilities;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Shadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * Graphical object representing a Stop in the graphical view.
 * A Stop is represented (in the graphical view) by an icon with a shape
 * pinpointing to the Stop's position on the map. It can also contain its order
 * number within the pointer.
 * <p>
 * Pickups Stops are represented by circle pointers.
 * Delivery Stops are represented by square pointers.
 * Warehouse Stops are represented by diamond pointers.
 */
public class GraphicalViewStop extends Pane implements Observer {

    /**
     * The center of the shape's X coordinate.
     */
    private double pointerCenterX;
    /**
     * The center of the shape's Y coordinate.
     */
    private double pointerCenterY;
    /**
     * The pointer's height.
     */
    private double pointerH;

    private Color fillColour;
    private Color outlineColour;

    /**
     * TextualViewStop constructor.
     * Populates the graphical object.
     * @param stop The corresponding Stop model object.
     * @param graphicSize The size of the pointer.
     * @param num The order number of the stop.
     */
    public GraphicalViewStop(final Stop stop, final double graphicSize, final int num) {

        stop.addObserver(this);

        fillColour = Color.BLACK;
        outlineColour = Color.BLACK;

        Shape symbol = new Circle();

        pointerCenterX = graphicSize / 2;
        pointerCenterY = graphicSize / 2;
        pointerH = graphicSize;

        double pointerX = graphicSize / 2;
        double pointerY = graphicSize / 2;

        switch (stop.getType()) {

            case PICKUP:
                fillColour = ViewUtilities.stringToColour(
                    stop.getRequest().getPickup().getAddress().toString()
                );
                symbol = new Circle(graphicSize / 2);
                symbol.setTranslateX(graphicSize / 2);
                symbol.setTranslateY(graphicSize / 2);
                double radius = graphicSize / 2;
                double theta = Math.acos(radius / pointerH);
                pointerX = radius * Math.sin(theta);
                pointerY = radius * Math.cos(theta);
                break;

            case DELIVERY:
                fillColour = ViewUtilities.stringToColour(
                    stop.getRequest().getPickup().getAddress().toString()
                );
                symbol = new Rectangle(graphicSize, graphicSize);
                break;

            case WAREHOUSE:
                outlineColour = Color.WHITE;
                symbol = new Rectangle(graphicSize, graphicSize);
                symbol.setRotate(45);
                pointerX = graphicSize / Math.sqrt(2);
                pointerY = 0;

        }

        Shape pointer = new Polygon(
            pointerCenterX - pointerX, pointerCenterY + pointerY,
            pointerCenterX + pointerX, pointerCenterY + pointerY,
            pointerCenterX, pointerCenterY + pointerH
        );

        Shape stopGraphic = Shape.union(symbol, pointer);
        stopGraphic.setFill(fillColour);
        stopGraphic.setStroke(outlineColour);
        stopGraphic.setStrokeWidth(graphicSize / 10);
        stopGraphic.setStrokeLineJoin(StrokeLineJoin.ROUND);

        this.getChildren().add(stopGraphic);

        if (num > 0) {
            int fontSize = (int) (graphicSize * 0.7);
            Text numText = new Text(num + "");
            numText.setFont(Font.font("Segoe UI", FontWeight.BOLD, fontSize));
            numText.setFill(Color.BLACK);
            numText.setTextAlignment(TextAlignment.CENTER);
            numText.relocate(
                pointerCenterX - numText.getBoundsInLocal().getWidth() / 2,
                pointerCenterY - numText.getBoundsInLocal().getHeight() / 2
            );
            this.getChildren().add(numText);
        }

    }

    /**
     * Relocates the graphical object to the given position.
     * @param pos The position to relocate the graphical object to.
     */
    public void place(final double[] pos) {
        relocate(
            pos[0] - pointerCenterX,
            pos[1] - pointerCenterY - pointerH
        );
    }

    @Override
    public void update(Observable observed, UpdateType updateType) {

        switch (updateType) {
            case STOP_HIGHLIGHT -> {
                Stop stop = (Stop)observed;
                if (stop.isHighlighted()) {
                    DropShadow shadow = new DropShadow();
                    shadow.setColor(outlineColour);
                    shadow.setRadius(0);
                    shadow.setSpread(1);
                    shadow.setOffsetY(-3);
                    this.setEffect(shadow);
                } else {
                    this.setEffect(null);
                }
            }
        }

    }
}
