package fr.uga.miage.m1.polygons.gui;

import fr.uga.miage.m1.polygons.gui.command.*;
import fr.uga.miage.m1.polygons.gui.shapes.SimpleShape;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Client implements MouseListener, MouseMotionListener {

    private CommandControl commandControl = new CommandControl();

    private JDrawingFrame frame;
    public Client(String title) {
        frame = new JDrawingFrame(title, this);

        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "undo");
        frame.getRootPane().getActionMap().put("undo", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {

                commandControl.addCommandFromHistory();
                commandControl.undoCommands();
            }
        });
    }

    public JDrawingFrame getFrame() {
        return frame;
    }

    //Souris
    /**
     * Implements method for the <tt>MouseListener</tt> interface to
     * draw the selected shape into the drawing canvas.
     * @param evt The associated mouse event.
     */
    public void mouseClicked(MouseEvent evt) {
        System.out.println("clicked");
        if(commandControl.getCommands() != null && commandControl.getCommands().size() > 0 && "cgroup".equalsIgnoreCase(commandControl.getLastCommand().getClass().getSimpleName())){
            commandControl.removeCommand(commandControl.getLastCommand());
        }

        int evtX = evt.getX();
        int evtY = evt.getY();

        if (frame.getPanel().contains(evtX, evtY)) {
            SimpleShape shape = ShapeFactory.getInstance().createShapeFromShapes(frame.getSelected(), evtX, evtY);
            commandControl.addCommand(new CShape(frame, shape, evtX, evtY));
            commandControl.executeCommands();
        }
    }

    /**
     * Implements an empty method for the <tt>MouseListener</tt> interface.
     * @param evt The associated mouse event.
     */
    public void mouseEntered(MouseEvent evt) {
        //TODO
    }

    /**
     * Implements an empty method for the <tt>MouseListener</tt> interface.
     * @param evt The associated mouse event.
     */
    public void mouseExited(MouseEvent evt) {
        frame.getLabel().setText(" ");
        frame.getLabel().repaint();
    }

    /**
     * Implements method for the <tt>MouseListener</tt> interface to initiate
     * shape dragging.
     * @param evt The associated mouse event.
     */
    public void mousePressed(MouseEvent evt) {
        System.out.println("pressed");
        if(frame.getPanel().contains(evt.getX(), evt.getY())){
            Point p = new Point(evt.getX(), evt.getY());
            frame.setLastPressed(p);
        }

        boolean shapeFound = false;

        if(!frame.getShapes().isEmpty()){
            for(SimpleShape mShape: frame.getShapes()){
                if(mShape.contains(evt.getX(),evt.getY())){
                    frame.setDraggedShape(mShape);
                    commandControl.addCommand(new DragNDrop(frame, mShape));
                    shapeFound = true;
                }
            }
        }

        if(!shapeFound){ //TODO tester si mode = cr�ation groupe plut�t que shapeFound
            int x = evt.getX();
            int y = evt.getY();

            commandControl.addCommand(new CGroup(frame, x, y));
        }
    }

    /**
     * Implements method for the <tt>MouseListener</tt> interface to complete
     * shape dragging.
     * @param evt The associated mouse event.
     */
    public void mouseReleased(MouseEvent evt) {
        System.out.println("released");
        if(frame.getDraggedShape() != null){
            commandControl.executeCommands();
            frame.setDraggedShape(null);
        }

        if("CGroup".equals(commandControl.getLastCommand().getClass().getSimpleName())){
            CGroup cGroup = (CGroup) commandControl.getLastCommand();
            cGroup.setX2(evt.getX());
            cGroup.setY2(evt.getY());

            commandControl.executeCommands();

        }
    }

    /**
     * Implements method for the <tt>MouseMotionListener</tt> interface to
     * move a dragged shape.
     * @param evt The associated mouse event.
     */
    public void mouseDragged(MouseEvent evt) {

        if(frame.getPanel().contains(evt.getX(), evt.getY()) && frame.getDraggedShape() != null){
            Point currentPoint = evt.getPoint();
            int x = currentPoint.x - frame.getDraggedShape().getX()-25;
            int y = currentPoint.y - frame.getDraggedShape().getY()-25;
            DragNDrop dragNDrop = (DragNDrop) commandControl.getLastCommand();
            dragNDrop.setNewX(x);
            dragNDrop.setNewY(y);
        }
    }

    /**
     * Implements an empty method for the <tt>MouseMotionListener</tt>
     * interface.
     * @param evt The associated mouse event.
     */
    public void mouseMoved(MouseEvent evt) {
        modifyLabel(evt);
    }

    //Graphique
    private void modifyLabel(MouseEvent evt) {
        frame.getLabel().setText("(" + evt.getX() + "," + evt.getY() + ")");
    }
}
