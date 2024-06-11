package Views;

import Entity.Entity;
import GameLogic.GameController;
import GameMap.Room;
import Item.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RoomView extends JPanel {

    /**
	 * Serial version ID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * A szoba képe, háttere mely csak a falakat valamit a padlót tartalmazza
	 */
	private transient Image roomImage;
	
	/**
	 * A szoba felső falára illeszkedő ajtók képe
	 */
    private transient Image topDoorImage;
    
    /**
     * A szoba aljára illeszkedő ajtók képe
     */
    private transient Image bottomDoorImage;
    
    /**
     * A szobában található többet ajtók jelzésére szolgáló piros ajtó képe
     */
    private transient Image redDoorImage;
    
    /**
     * A szobában található többlet karakterek helyett megjelenítendő sátor képe
     */
    private transient Image tentImage;
    
    /**
     * A szobában található többlet tárgyak helyett megjelenítendő sátor képe
     */
    private transient Image chestImage;
    
    /**
     * A szoba gázzal telítettségét jelző háromszög ikon
     */
    private transient Image toxicRoomIcon;
    
    /**
     * A szoba padlójának ragacsosságát jelző háromszög ikon
     */
    private transient Image stickyFloorIcon;
    
    /**
     * Az alsó ajtó szélességének értéke pixelekben
     */
    private int bdWidthInPixels;
    
    /**
     * Az alsó ajtó magasságának értéke pixelekben
     */
    private int bdHeightInPixels;

    /**
     * A felső ajtó kirajzolásához használható legkisebb x koordináta értéke
     */
    private int topDoorMinXPosition;
    
    /**
     * A felső ajtó kirajzolásához használható legnagyobb x koordináta értéke
     */
    private int topDoorMaxXPosition;
    
    /**
     * A felső ajtó krajzolásához használandó y koordináta értéke
     */
    private int topDoorYPostition;
    
    /**
     * A szobában megjelenő szövegek betűtípusa
     */
    private String fontName = "feel default";

    /**
     * A megjelenítendő szoba referenciája
     */
    private Room currentRoom;

    /**
     * A szobát grafikusan megjelenító JPanel leszármazott RoomView osztály konstruktora
     * @param currentRoom Az elsőként megjelenítendő szoba referenciája
     */
    public RoomView(Room currentRoom) {
        this.setSize(512, 512);

        this.currentRoom = currentRoom;

        roomImage = new ImageIcon("./rsc/clearEmptyRoom.png").getImage();
        topDoorImage = new ImageIcon("./rsc/resizedDoor.png").getImage();
        bottomDoorImage = new ImageIcon("./rsc/doorBottom.png").getImage();
        redDoorImage = new ImageIcon("./rsc/redDoor.png").getImage();
        tentImage = new ImageIcon("./rsc/tent.png").getImage();
        chestImage = new ImageIcon("./rsc/chest.png").getImage();
        toxicRoomIcon = new ImageIcon("./rsc/Toxic.png").getImage();
        stickyFloorIcon = new ImageIcon("./rsc/Sticky.png").getImage();
        bdHeightInPixels = (int) ( bottomDoorImage.getHeight(null) * 0.46);
        bdWidthInPixels = (int) ( bottomDoorImage.getWidth(null) * 0.35);
        
        topDoorMinXPosition = 15;
        topDoorMaxXPosition = 512;
        topDoorYPostition = 107;
    }

    /**
     * A szoba kirajzolásához használt metódus. A megadott szoba szerint frissíti a panel tartalmát
     */
    @Override
    public void paint(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;

        g2D.drawImage(roomImage, 0, 0, 512, 512, null);

        drawDoors(g2D, currentRoom.getDoors().size() );
        drawEntities(g2D, currentRoom.getEntitesInside());
        drawItems(g2D, currentRoom.getContainedItems());
        drawRoomStatusIcons(g2D);

    }

    /**
     * Újrarajzolás igényléséhez használandó metódus. Meghívása során frissíti a panel tartalmát
     */
    public void update() { 
    	currentRoom = GameController.getInstance().getCurrentEntity().getCurrentRoom();
    	this.repaint();
    	this.revalidate();
    }

    /**
     * Megváltoztatja a kirajzolandó szobát a paraméterül átadott szobára,
     * valamint frissíti a panel tartalmát
     * @param rvm A kirajzolandó szoba referenciája
     */
    public void changeDisplayRoom(Room rvm) {
        currentRoom = rvm;
        this.update();
    }

    /**
     * A paraméterül átadott Graphics2D komponensre a szintén paraméterül átadott tárgyakat
     * kirajzolja egy rendezett rácson. Ha a tárgyak száma meghaladja a 8 darabot, kizárólag 6 tárgy 
     * kerül a földre, a többi egy ládába kerül. A láda fölötti szám jelzi, hogy "hány darab tárgy 
     * van a ládában"
     * @param g A Graphics2D komponens amire a tárgyak kirajzolása történik
     * @param items A kirajzolandó táárgyak referenciáinak listája
     */
    private void drawItems(Graphics2D g, List<Item> items) {

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
            g.setFont(new Font(fontName, Font.BOLD, 20));
            g.drawString("+" + itemOverflowNum , itemXPosititon + 40 , itemYPosition + 35);
        }

    }

    /**
     * A paraméterül átadott Graphics2D komponensre a szintén paraméterül átadott karaktereket
     * kirajzolja egy rendezett rácson. Ha a karakterek száma meghaladja a 15 darabot, kizárólag
     * 14 karakter kerül a kirajzolásra, a többi egy sátorba kerül. A sátor fölötti szám jelzi,
     * hogy "hány darab karakter van a sátorban"
     * @param g A Graphics2D komponens amire a tárgyak kirajzolása történik
     * @param entities A kirajzolandó karakterek referenciáinak listája
     */
    private void drawEntities(Graphics2D g, java.util.List<Entity> entities) {

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
                    g.setFont(new Font(fontName, Font.BOLD, 15));
                    g.drawString("+" + entityOverFlownum , entityNextXPosition + 25 , entityNextYPosition + 55);
                }else {
                    g.setFont(new Font(fontName, Font.BOLD, 15));
                    Color nameBackgroundColor = (current == GameController.getInstance().getCurrentEntity()) ? Color.blue : Color.gray ;
                    g.setPaint(nameBackgroundColor);
                    
                    //Túl hosszú név levágása
                    String displayName = (current.getName().length() > 8) ? current.getName().substring(0, 6) + "."  : current.getName();
                    
                    //Szöveg és kitöltött ovális eltolásának meghatározásához végzett számítások
                    FontMetrics fontMetrics = g.getFontMetrics();
                    int currWidth = current.getImage().getWidth(null);
                    int nameWidth = fontMetrics.stringWidth(displayName);
                    int stringOffset = (currWidth - nameWidth) / 2;
                                        
                    //Tényleges kirajzolás
                    g.fillOval(entityNextXPosition + stringOffset - 7, entityNextYPosition - 23, fontMetrics.stringWidth(displayName) + 14, fontMetrics.getHeight() + 5);
                    g.setPaint(Color.white);
                    g.drawString(displayName , entityNextXPosition + stringOffset , entityNextYPosition - 5);
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

    /**
     * A szobában található ajtók vizualizálására szolgáló metódus. A paraméterben
     * átadott Graphics2D komponensre kirajzol a szintén paraméterül átadott számú
     * ajtót rendezetten. Először a felső ajtókat jeleníti meg, majd hozzáveszi az
     * alsókat is. Ha túl sok ajtó lenne egy szobában, a felső ajtók közül az egyiket
     * kicseréli pirosra, valamint a felette található számmal jelzi, hogy a piros 
     * ajtó hány másik ajtóhoz "biztosít elérhetőséget"
     * @param g A Graphics2D komponens amire az ajtók kirajzolása történik
     * @param numberOfdoors Kirajzolandó ajtók száma
     */
    private void drawDoors(Graphics2D g, int numberOfdoors) {

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
                g.setFont(new Font(fontName, Font.BOLD, 24));
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
    
    /**
     * A szoba a játékos számára releváns tulajdonságait megjelenító metódus.
     * Ilyen tulajdonságok a szoba gázzal telítettsége, padlójának ragacsossága,
     * valamint a szoba azonosítójának száma.
     * @param g A Graphics2D komponens amire az információk kirajzolása történik
     */
    private void drawRoomStatusIcons(Graphics2D g) {
    	int startPosition = 420;
    	int offset = 50;
    	
    	if(GameController.getInstance().getCurrentEntity().getCurrentRoom().getIsToxic()) {
    		g.drawImage(toxicRoomIcon, startPosition, 60, null);
    		startPosition += offset;
    	}
    	
    	if(GameController.getInstance().getCurrentEntity().getCurrentRoom().getIsSticky()) {
    		g.drawImage(stickyFloorIcon, startPosition, 63, null);
    	}
    	
    	g.setPaint(Color.black);
    	g.setFont(new Font(fontName, Font.BOLD, 18));
    	g.drawString("#" + GameController.getInstance().getCurrentEntity().getCurrentRoom().getID(), 45, 90);
    	
    }
}