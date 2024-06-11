package Item;

import java.io.Serializable;

import javax.swing.ImageIcon;

import GameLogic.*;
import Entity.*;
import Interfaces.*;

public class Mask extends Item implements Debuggable, Serializable {
    
	/**
	 * Serial Version ID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
     * Megadja hogy az előző szoba mérgező volt-e
     */
    private boolean lastRoomWasToxic;

    /**
     * Az osztály konstruktora, beállítja a tárgy ID-ját.
     * @param aID - A tárgy ID-ja.
     */
    public Mask(int aID) {
        super(aID);
        setDurability(5);
        GameController.getInstance().debuggableObjects.put(aID, this);
        image = new ImageIcon("./rsc/mask.png").getImage();
    }       

    /**
     * A lastRoomWasToxic attribútum gettere.
     * @return - lastRoomWasToxic attribútum
     */
    public boolean getLastRoomWasToxic(){
        return lastRoomWasToxic;
    }

    /**
     * A lastRoomWasToxic attribútum settere.
     * @param b - A beállítandó érték az attribútumnak.
     */
    public void setLastRoomWasToxic(boolean b){
        lastRoomWasToxic = b;
    }

    /**
     * A függvény célja, hogy a saját referenciájával tudja meghívni a paraméterként kapott Entity.Entity “pickUpItem” rá vonatkozó változtatát. (visitor pattern)
     * @param e - Az Entity.Entity, aki felveszi a maszkot.
     * @return - Az Entity.Entity erre a tárgyra használt pickUpItem függvényének visszatérési értéke.
     */
    @Override
    public boolean pickUp(Entity e) {
        return e.pickUpItem(this);
    }

    /**
     * A függvény célja, hogy a saját referenciájával tudja meghívni a paraméterként kapott Entity.Entity “dropItem” rá vonatkozó változtatát. (visitor pattern)
     * @param e - Az Entity.Entity, aki lerakja a maszkot.
     * @return - Az Entity.Entity erre a tárgyra használt drop() függvényének visszatérési értéke.
     */
    @Override
    public boolean drop(Entity e) {
        return e.dropItem(this);
    }  
    
    /**
     * A gáz által okozott bénítás kivédését végzi. Ellenőrzi, hogy a maszk tartalmaz-e még felhasználható töltetet.
     * Ha igen a lastRoomWasToxic értékétől függően azt vizsgálja meg, hogy ez a fennmaradó töltet elegendő-e a bénítás kivédésére.
     * Ugyanis, ha az előző szoba gázos volt (lastRoomWasToxic == true) akkor 2 töltet szükséges ehhez, egyébként 1.
     * Ha van elegendő, a szükséges mennyiséget levonja, beállítja a lastRoomWasToxic értékét igazra és visszatér „true”-val.
     *  Ha nincs elegendő, a töltet szám nullázódik és a visszatérési érték „false”.
     *  @return - A gáz elleni védekezés logikai értéke.
     */
    @Override
    public boolean preventGasStun() {
    	
    	if (getDurability() > 0) {
            if (getLastRoomWasToxic()) {
                if(getDurability() < 2) return false;
                setDurability(getDurability() - 2);
            } else {
                setDurability(getDurability() - 1);
                setLastRoomWasToxic(true);
            }
            return true;
        }
        return false;
    }
    
    /**
     * Egy maszk tárgy normál szobába való bevitelével járó állapotváltozását kezeli,
     * azaz beállítja a lastRoomWasToxic taváltozó értékét „false” értékűre.
     */
    @Override
    public void carriedIntoNormalRoom() {
    	setLastRoomWasToxic(false);
    }

    /**
     * Visszatér egy Stringgel ami a maga nevéből és az azonosítójából áll
     * @return - A String
     */
    @Override
    public String toString(){
        return "Mask #" + Integer.toString(getID());
    }
    
    /**
     * Debug szöveg generálása
     * @param cmdInput - ezzel tud ID-t ellenőrizni
     * @return - Az objektum állapotának szöveges reprezentációja
     */
    @Override
    public String debug() {
        return "---- Item.Mask " + this.getID() + " ----\ndurability : " + getDurability() + "\nlastRoomWasToxic : " + lastRoomWasToxic +"\n---- Item.Mask " + this.getID() + " ----\n";
    }

    /**
     * Ezzel az itemmel nem lehet connect-elni, így false a visszatérés.
     */
    @Override
    public boolean connect(Item i, Entity ent) {
        return false;
    }

    /**
     * Ezzel az itemmel nem lehet disconnectelni, így false a visszatérés.
     */
    @Override
    public boolean disconnect(Item i) {
        return false;
    }


    /**
     * Ezzel az itemmel nem lehet connect-elni, így false a visszatérés.
     */
    @Override
    public boolean TransistorConnect(Transistor t, Entity e) {
        return false;
    }

    /**
     * Ezzel az itemmel nem lehet disconnectelni, így false a visszatérés.
     */
    @Override
    public boolean TransistorDisconnect(Transistor t) {
        return false;
    }
}
