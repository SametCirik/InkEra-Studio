package com.inkera.util;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ResizeHelper {

    private static final int RESIZE_MARGIN = 8;

    public static void addResizeListener(Stage stage) {
        ResizeListener resizeListener = new ResizeListener(stage);

        stage.getScene().addEventHandler(MouseEvent.MOUSE_MOVED, resizeListener);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_PRESSED, resizeListener);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_DRAGGED, resizeListener);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_EXITED, resizeListener);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, resizeListener);
    }

    private static class ResizeListener implements EventHandler<MouseEvent> {

        private final Stage stage;
        private Cursor cursorEvent = Cursor.DEFAULT;
        private double startX = 0;
        private double startY = 0;

        ResizeListener(Stage stage) {
            this.stage = stage;
        }

        @Override
        public void handle(MouseEvent event) {

            double mouseX = event.getSceneX();
            double mouseY = event.getSceneY();
            double width = stage.getWidth();
            double height = stage.getHeight();

            if (MouseEvent.MOUSE_MOVED.equals(event.getEventType())) {
                if (mouseX < RESIZE_MARGIN && mouseY < RESIZE_MARGIN) {
                    cursorEvent = Cursor.NW_RESIZE;
                } else if (mouseX < RESIZE_MARGIN && mouseY > height - RESIZE_MARGIN) {
                    cursorEvent = Cursor.SW_RESIZE;
                } else if (mouseX > width - RESIZE_MARGIN && mouseY < RESIZE_MARGIN) {
                    cursorEvent = Cursor.NE_RESIZE;
                } else if (mouseX > width - RESIZE_MARGIN && mouseY > height - RESIZE_MARGIN) {
                    cursorEvent = Cursor.SE_RESIZE;
                } else if (mouseX < RESIZE_MARGIN) {
                    cursorEvent = Cursor.W_RESIZE;
                } else if (mouseX > width - RESIZE_MARGIN) {
                    cursorEvent = Cursor.E_RESIZE;
                } else if (mouseY < RESIZE_MARGIN) {
                    cursorEvent = Cursor.N_RESIZE;
                } else if (mouseY > height - RESIZE_MARGIN) {
                    cursorEvent = Cursor.S_RESIZE;
                } else {
                    cursorEvent = Cursor.DEFAULT;
                }
                stage.getScene().setCursor(cursorEvent);
            }

            else if (MouseEvent.MOUSE_PRESSED.equals(event.getEventType())) {
                startX = stage.getWidth() - mouseX;
                startY = stage.getHeight() - mouseY;
            }

            else if (MouseEvent.MOUSE_DRAGGED.equals(event.getEventType())) {

                if (cursorEvent == Cursor.DEFAULT) {
                    return;
                }

                if (cursorEvent == Cursor.W_RESIZE || cursorEvent == Cursor.NW_RESIZE || cursorEvent == Cursor.SW_RESIZE) {
                    double newWidth = stage.getX() - event.getScreenX() + stage.getWidth();
                    if (newWidth > stage.getMinWidth()) {
                        stage.setX(event.getScreenX());
                        stage.setWidth(newWidth);
                    }
                }

                if (cursorEvent == Cursor.E_RESIZE || cursorEvent == Cursor.NE_RESIZE || cursorEvent == Cursor.SE_RESIZE) {
                    double newWidth = mouseX + startX;
                    if (newWidth > stage.getMinWidth()) {
                        stage.setWidth(newWidth);
                    }
                }

                if (cursorEvent == Cursor.N_RESIZE || cursorEvent == Cursor.NE_RESIZE || cursorEvent == Cursor.NW_RESIZE) {
                    double newHeight = stage.getY() - event.getScreenY() + stage.getHeight();
                    if (newHeight > stage.getMinHeight()) {
                        stage.setY(event.getScreenY());
                        stage.setHeight(newHeight);
                    }
                }

                if (cursorEvent == Cursor.S_RESIZE || cursorEvent == Cursor.SE_RESIZE || cursorEvent == Cursor.SW_RESIZE) {
                    double newHeight = mouseY + startY;
                    if (newHeight > stage.getMinHeight()) {
                        stage.setHeight(newHeight);
                    }
                }
            }

            else if (MouseEvent.MOUSE_EXITED.equals(event.getEventType())
                    || MouseEvent.MOUSE_EXITED_TARGET.equals(event.getEventType())) {
                stage.getScene().setCursor(Cursor.DEFAULT);
            }
        }
    }
}
