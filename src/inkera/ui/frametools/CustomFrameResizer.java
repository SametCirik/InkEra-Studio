package inkera.ui.frametools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Supplier;

/**
 * Çerçevesiz (undecorated) JFrame'leri kenarlarından sürükleyerek yeniden
 * boyutlandırmayı sağlayan yardımcı sınıf.
 */
public class CustomFrameResizer extends MouseAdapter {

    private final JFrame ownerFrame;
    private final int borderThickness;
    private final Dimension minSize;
    private final Supplier<Boolean> isPanningSuppressed;

    private Cursor currentCursor = Cursor.getDefaultCursor();
    private Point startDragPoint;
    private int resizeDirection = -1;

    // Resize yönleri için sabitler
    private static final int NORTH = 1;
    private static final int SOUTH = 2;
    private static final int WEST = 4;
    private static final int EAST = 8;
    private static final int NORTH_WEST = NORTH | WEST;
    private static final int NORTH_EAST = NORTH | EAST;
    private static final int SOUTH_WEST = SOUTH | WEST;
    private static final int SOUTH_EAST = SOUTH | EAST;

    public CustomFrameResizer(JFrame frame, int borderThickness, int minWidth, int minHeight, Supplier<Boolean> panningSupressor) {
        this.ownerFrame = frame;
        this.borderThickness = borderThickness;
        this.minSize = new Dimension(minWidth, minHeight);
        this.isPanningSuppressed = (panningSupressor != null) ? panningSupressor : () -> false;
        
        ownerFrame.addMouseListener(this);
        ownerFrame.addMouseMotionListener(this);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (isPanningSuppressed.get()) {
            ownerFrame.setCursor(Cursor.getDefaultCursor());
            return;
        }

        Rectangle frameBounds = ownerFrame.getBounds();
        Point mousePoint = e.getPoint();
        int direction = 0;

        if (mousePoint.y >= 0 && mousePoint.y < borderThickness) {
            direction |= NORTH;
        }
        if (mousePoint.y >= frameBounds.height - borderThickness && mousePoint.y < frameBounds.height) {
            direction |= SOUTH;
        }
        if (mousePoint.x >= 0 && mousePoint.x < borderThickness) {
            direction |= WEST;
        }
        if (mousePoint.x >= frameBounds.width - borderThickness && mousePoint.x < frameBounds.width) {
            direction |= EAST;
        }

        Cursor newCursor;
        switch (direction) {
            case NORTH:       newCursor = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR); break;
            case SOUTH:       newCursor = Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR); break;
            case WEST:        newCursor = Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR); break;
            case EAST:        newCursor = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR); break;
            case NORTH_WEST:  newCursor = Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR); break;
            case NORTH_EAST:  newCursor = Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR); break;
            case SOUTH_WEST:  newCursor = Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR); break;
            case SOUTH_EAST:  newCursor = Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR); break;
            default:          newCursor = Cursor.getDefaultCursor();
        }

        if (newCursor != ownerFrame.getCursor()) {
            ownerFrame.setCursor(newCursor);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (ownerFrame.getCursor() != Cursor.getDefaultCursor()) {
            startDragPoint = e.getLocationOnScreen();
            resizeDirection = getResizeDirection(e.getPoint());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        startDragPoint = null;
        resizeDirection = -1;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (startDragPoint == null || resizeDirection == -1) {
            return;
        }

        Point currentPoint = e.getLocationOnScreen();
        int dx = currentPoint.x - startDragPoint.x;
        int dy = currentPoint.y - startDragPoint.y;

        Rectangle bounds = ownerFrame.getBounds();
        int newX = bounds.x;
        int newY = bounds.y;
        int newWidth = bounds.width;
        int newHeight = bounds.height;

        if ((resizeDirection & WEST) != 0) {
            newX += dx;
            newWidth -= dx;
        }
        if ((resizeDirection & EAST) != 0) {
            newWidth += dx;
        }
        if ((resizeDirection & NORTH) != 0) {
            newY += dy;
            newHeight -= dy;
        }
        if ((resizeDirection & SOUTH) != 0) {
            newHeight += dy;
        }

        // Minimum boyut kontrolü
        if (newWidth < minSize.width) {
            if ((resizeDirection & WEST) != 0) newX = bounds.x + bounds.width - minSize.width;
            newWidth = minSize.width;
        }
        if (newHeight < minSize.height) {
            if ((resizeDirection & NORTH) != 0) newY = bounds.y + bounds.height - minSize.height;
            newHeight = minSize.height;
        }

        ownerFrame.setBounds(newX, newY, newWidth, newHeight);
        startDragPoint = currentPoint;
    }

    private int getResizeDirection(Point p) {
        int direction = 0;
        Rectangle bounds = ownerFrame.getBounds();
        if (p.y < borderThickness) direction |= NORTH;
        if (p.y > bounds.height - borderThickness) direction |= SOUTH;
        if (p.x < borderThickness) direction |= WEST;
        if (p.x > bounds.width - borderThickness) direction |= EAST;
        return direction;
    }
}
