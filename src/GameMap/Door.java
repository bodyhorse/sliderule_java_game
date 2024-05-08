package GameMap;

import Interfaces.Debuggable;

import java.io.Serializable;

import GameMap.*;
import Entity.*;
import GameLogic.*;


public class Door implements Debuggable, Serializable {
    
	/**
	 * Default serial version ID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
     * Az ajtó egyedi azonosítója
     */
    private int ID;

    /**
     * Az ajtó irányát tároló enum érékek lehetséges változatai.
     */
     public enum Direction{LEFT,RIGHT,BOTH}

    /**
     * Megadja hogy az ajtó elátkozott-e.
     */
    private boolean isCursed;
    
    /**
     * Az ajtó szobái
     */
    private Room[] availableRooms;
    
    /**
     * Direction enum szerinti irány
     */
    private Direction direction;

    /**
     * Az ajtó egyedi azonosítóját adja vissza
     * @return - az ID
     */
    public int getID() {
        return ID;
    }

    /**
     * Az ajtó egyedi azonosítóját állítja
     * @param id - az ID
     */
    public void setID(int id) {
        ID = id;
    }

    /**
     * Vissza adja hogy az ajtó elátkozott-e.
     * @return - Az ajtó elátkozottsága.
     */
    public boolean getIsCursed(){ return isCursed; }

    /**
     * Az isCursed setter függvénye
     * @param c - erre az értékre állítja az isCursed értéket
     */
    public void setIsCursed(boolean c){ isCursed = c; }

    /**
     * Az ajtó 1-es számú szobájának getter függvénye
     * @return - 1-es számú szoba
     */
    public Room getRoomOne(){ return availableRooms[0]; }
    /**
     * Az ajtó 2-es számú szobájának getter függvénye
     * @return - 2-es számú szoba
     */
    public Room getRoomTwo(){ return availableRooms[1]; }

    /**
     * Az ajtó 1-es számú szobájának setter függvénye
     * @param newR - ezt a szobát állítja be az egyes számú szobának.
     */
    public void setRoomOne(Room newR){ availableRooms[0] = newR; }
    /**
     * Az ajtó 2-es számú szobájának setter függvénye
     * @param newR - ezt a szobát állítja be az kettes számú szobának.
     */
    public void setRoomTwo(Room newR){ availableRooms[1] = newR; }

    /**
     * Az ajtó irányának getter függvénye
     * @return - ajtó iránya
     */
    public Direction getDirection(){ return direction; }

    /**
     * Az ajtó iráyának setter függvénye
     * @param d - beállítandó irány
     */
    public void setDirection(Direction d){ direction = d; }

    /**
     * Az ajtó konstruktora
     * @param r1 - 1. számú szoba
     * @param r2 - 2. számú szoba
     * @param cursed - elátkozott-e az ajtó
     * @param d - az ajtó iránya
     */
    public Door(Room r1, Room r2, boolean cursed, Direction d, int dID){
        availableRooms = new Room[2];
        availableRooms[0] = r1;
        availableRooms[1] = r2;
        isCursed = cursed;
        direction = d;
        ID = dID;
        GameController.getInstance().debuggableObjects.put(ID, this);
    }

    /**
     * Az Entity.Entity tovább haladását bonyolítja le az ajtón keresztül
     * @param ent - továbbhaladni akaró Entity.Entity
     * @param curr - Entity.Entity jelenlegi szobája
     * @return - az áthaladás sikerességénének logikai értéke
     */
    public boolean moveEntity(Entity ent, Room curr){
    	boolean res = false;
    	
    	if(!this.getIsCursed()){
            if(curr == availableRooms[0]) {
                if(direction == Direction.RIGHT || direction == Direction.BOTH) {
                    res = availableRooms[1].acceptEntity(ent);
                }else GameController.getInstance().errorMsg(303, String.valueOf(ID));
            }else if(curr == availableRooms[1]) {
                if(direction == Direction.LEFT || direction == Direction.BOTH) {
                    res = availableRooms[0].acceptEntity(ent);
                }else GameController.getInstance().errorMsg(303, String.valueOf(ID));
            }else {
                return false;
            }
        }else GameController.getInstance().errorMsg(304, String.valueOf(ID));
        
        return res;
    }

    /**
     * Visszatér egy Stringgel, az azonosítójával kiegészítve
     * @return - A String
     */
    public String toString(){
        return "GameMap.Door: #" + Integer.toString(getID());
    }

    /**
     * Visszatér egy Stringgel ami azt tartalmazza, hogy melyik szobákat köti össze. Megadunk neki egy szobát, ez alapján tudja
     * hogy a másik szobát kell visszaadnia
     * @param room - a megadott szoba
     * @return - a másik szoba Stringként, ID-vel együtt.
     */
    public String getCurrentDirection(Room room){
        Room leadingTo;
        if(availableRooms[0] == room)
            leadingTo = availableRooms[1];
        else
            leadingTo = availableRooms[0];
        return "GameMap.Room #" + Integer.toString(leadingTo.getID());
    }

    /**
     * Ajtó tartalmazását ellenőrző függvény
     * @param r1 - az ajtó amit keresünk
     * @return - Ha az ajtó utat biztosít az adott ajtó felé akkor "true" értékkel tér vissza
     */
    public boolean containsRoom(Room r1){
        if(availableRooms[0] == r1 || availableRooms[1] == r1) return true;
        return false;
    }

    /**
     * Megnézi hogy melyik szobája a curr és vissza adja a másikat
     * @param curr - szoba aminek a párját keressük
     * @return - pár
     */
    public Room neighbourRoom(Room curr){
        if(getRoomOne() == curr) return getRoomTwo();
        else return getRoomOne();
    }

    /**
     * Debug szöveg generálása
     * @return - Az objektum állapotának szöveges reprezentációja
     */
    @Override
    public String debug() {
        String retVal = "---- GameMap.Door " + this.getID() + " ----\nisCursed : " + isCursed + "\ndirection : " + direction + "\nAvailableRooms :";
        for(Room r: availableRooms){
            retVal += "\n - " + r.getID();
        }
        retVal += "\n---- GameMap.Door " + this.getID() + " ----\n";
        return retVal;
    }
}
