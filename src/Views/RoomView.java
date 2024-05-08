package Views;

import Entity.Entity;
import GameMap.Room;
import Interfaces.*;
import Item.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class RoomView extends JPanel {

    private Image roomImage;
    private Image topDoorImage; //50 95
    private Image bottomDoorImage;
    private Image redDoorImage;
    private Image tentImage;
    private Image chestImage;
    private int bdWidthInPixels;
    private int bdHeightInPixels;

    private int topDoorMinXPosition;
    private int topDoorMaxXPosition;
    private int topDoorYPostition;
    private int distanceBetweenTopDoors;
    private int maxNoOfTopDoors;

    private Room currentRoom;

    public RoomView(Room currentRoom) {
        this.setSize(512, 512);

        this.currentRoom = currentRoom;

        roomImage = new ImageIcon("rsc/clearEmptyRoom.png").getImage();
        topDoorImage = new ImageIcon("rsc/resizedDoor.png").getImage(); //logarlec/rsc/resizedDoor.png
        bottomDoorImage = new ImageIcon("rsc/doorBottom.png").getImage(); //logarlec/rsc/doorBottom.png
        redDoorImage = new ImageIcon("rsc/redDoor.png").getImage(); //logarlec/rsc/redDoor.png
        tentImage = new ImageIcon("rsc/tent.png").getImage(); // logarlec/rsc/tent.png
        chestImage = new ImageIcon("rsc/chest.png").getImage(); //logarlec/rsc/chest.png
        bdHeightInPixels = (int) ( bottomDoorImage.getHeight(null) * 0.46);
        bdWidthInPixels = (int) ( bottomDoorImage.getWidth(null) * 0.35);
        

        topDoorMinXPosition = 15; //40;
        topDoorMaxXPosition = 512; //40;
        topDoorYPostition = 107; //93;
        distanceBetweenTopDoors = 70;//72;
        maxNoOfTopDoors = 6;
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;

        g2D.drawImage(roomImage, 0, 0, 512, 512, null);

        drawDoors(g2D, currentRoom.getDoors().size() );
        drawEntities(g2D, currentRoom.getEntitesInside());
        drawItems(g2D, currentRoom.getContainedItems());

    }

    public void update() { 
    	this.revalidate();
    	this.repaint();}

    public void changeDisplayRoom(Room rvm) {
        currentRoom = rvm;
        this.update();
    }

    public void drawItems(Graphics2D g, List<Item> items) {

        int numberOfItems = items.size();
        boolean itemOverflow = false;
        int itemOverflowNum = 0;

        if(numberOfItems > 8) {
            itemOverflow = true;
            itemOverflowNum = numberOfItems - 6;

            numberOfItems = 6;
        }

        int itemYPosition = 190;
        int itemXPosititon = 400;
        int verticalSpacing = 27;

        for(int i = 0; i < numberOfItems; ++i) {
            int itemXOffset = (i % 2 == 0) ? 30 : -30;
            g.drawImage(items.get(i).getImage(), itemXPosititon + itemXOffset, itemYPosition, null);
            itemYPosition += verticalSpacing;
        }

        if(itemOverflow) {
            g.drawImage(chestImage, itemXPosititon + 20, itemYPosition + 30, null);
            g.setPaint(Color.white);
            g.setFont(new Font("feel default", Font.BOLD, 20));
            g.drawString("+" + itemOverflowNum , itemXPosititon + 40 , itemYPosition + 35);
        }

    }

    public void drawEntities(Graphics2D g, java.util.List<Entity> entities) {

        int verticalSpacing = 70;
        int horizontalScaping = 93;
        int entityXStartPosition = 45;
        int entityYStartPosition = 170;
        boolean entityOverFlow = false;
        int entityOverFlownum = 0;

        int countOfEntities = entities.size();

        if(countOfEntities > 15) {
            entityOverFlow = true;
            entityOverFlownum = countOfEntities - 14;
            countOfEntities = 15;
        }

        int entityIndex = 0;


        int entityNextXPosition = entityXStartPosition;
        int entityNextYPosition = entityYStartPosition;

        int posInRow = 0;


        while (countOfEntities > 0) {
            if(posInRow < 5) {
                Entity current = entities.get(entityIndex++);
                if(countOfEntities == 1 && entityOverFlow) {
                    g.drawImage(tentImage, entityNextXPosition - 25, entityNextYPosition, null);
                    g.setPaint(Color.black);
                    g.setFont(new Font("feel default", Font.BOLD, 15));
                    g.drawString("+" + entityOverFlownum , entityNextXPosition + 25 , entityNextYPosition + 55);
                }else {
                    g.setFont(new Font("feel default", Font.BOLD, 15));
                    g.setPaint(Color.gray);
                    //Szöveggel való skálázódást még ki kell számolni
                    g.fillOval(entityNextXPosition - 5, entityNextYPosition - 23, current.getName().length() * 10, 25);
                    g.setPaint(Color.white);
                    g.drawString(current.getName() , entityNextXPosition , entityNextYPosition - 5);
                    g.drawImage(current.getImage(), entityNextXPosition, entityNextYPosition, null);
                }
                posInRow++;
                countOfEntities--;
                entityNextXPosition += verticalSpacing;
            }else {
                entityNextYPosition += horizontalScaping;
                entityNextXPosition = entityXStartPosition;
                posInRow = 0;
            }
        }


    }

    public void drawDoors(Graphics2D g, int numberOfdoors) {

        int doorsToTop = 0;
        int doorsToBottom = 0;
        int doorOverflowNum = 0;
        boolean doorOverflow = false;

        if(numberOfdoors > 12) {
            doorOverflow = true;
            doorOverflowNum = numberOfdoors - 12;
            numberOfdoors = 12;
        }

        if(numberOfdoors <= 8) {
            if(numberOfdoors <= 4) {
                doorsToTop = numberOfdoors;
            }
            else {
                doorsToTop = 4;
                doorsToBottom = numberOfdoors - 4;
            }
        }else {
            if((numberOfdoors - 8) <= 2) {
                doorsToTop = 4 + numberOfdoors - 8;
                doorsToBottom = 4;
            }else {
                doorsToTop = 6;
                doorsToBottom = 4 + numberOfdoors - 8 - 2;
            }
        }

        int topDoorMiddlePoint = (topDoorMaxXPosition - topDoorMinXPosition) / (doorsToTop + 1);
        int bottomDoorMiddlePoint = (topDoorMaxXPosition - topDoorMinXPosition) / (doorsToBottom + 1);

        int nextTopDoorXPosition = topDoorMiddlePoint + topDoorMinXPosition - 30;
        for(int i = 0; i < doorsToTop; ++i) {
            if(doorOverflow && i == 3) {
                g.drawImage(redDoorImage, nextTopDoorXPosition, topDoorYPostition, null);
                g.setFont(new Font("feel default", Font.BOLD, 24));
                int numberoffset = (doorOverflowNum > 10) ? 5 : 15;
                g.drawString("+" + doorOverflowNum, nextTopDoorXPosition + numberoffset, topDoorYPostition - 10);
            }else
                g.drawImage(topDoorImage, nextTopDoorXPosition, topDoorYPostition, null);

            nextTopDoorXPosition += topDoorMiddlePoint;
        }

        int nextBottomDoorXPosition = bottomDoorMiddlePoint + topDoorMinXPosition - 30;
        for(int i = 0; i < doorsToBottom; ++i) {
            g.drawImage(bottomDoorImage, nextBottomDoorXPosition, 450,  bdWidthInPixels, bdHeightInPixels, null);
            nextBottomDoorXPosition += bottomDoorMiddlePoint;
        }
    }
}