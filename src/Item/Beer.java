package Item;

import java.io.Serializable;

import javax.swing.ImageIcon;

import GameLogic.*;
import GameMap.*;
import Entity.*;
import Item.*;
import Interfaces.*;

public class Beer extends Item implements Decaying, Debuggable, Serializable {
    private boolean isActive;

    /**
     * Az osztály konstruktora, beállítja a tárgy ID-ját.
     * @param aID - A tárgy ID-ja.
     */
    public Beer(int aID) {
        super(aID);
        setDurability(5);
        GameController.getInstance().debuggableObjects.put(aID, this);
        image = new ImageIcon("rsc/beer.png").getImage();
    }

    /**
     * Az isActive attribútum gettere
     * @return - Az isActive attribútum értéke
     */
    public boolean getIsActive(){
        return isActive;
    }

    /**
     * Az isActive attribútum settere
     * @param b - A beállítandó isActive érték
     */
    public void setIsActive(boolean b){
        isActive = b;
    }

    /**
     * A függvény célja, hogy aktivizálja a sört.
     * @param r - A szoba, ahol használva lesz a tárgy.
     * @param e - Az Entity.Entity, aki használja.
     * @return - True, ha sikeres a használat, False egyéb esetben.
     */
    @Override
    public boolean use(Room r, Entity e){
        if(isActive) return false;
        this.setIsActive(true);
        return true;
    }

    /**
     * A függvény célja, hogy a saját referenciájával tudja meghívni a paraméterként kapott Entity.Entity “pickUpItem” rá vonatkozó változtatát. (visitor pattern)
     * @param e - Az Entity.Entity, aki felveszi a sört.
     * @return - Az Entity.Entity erre a tárgyra használt pickUpItem függvényének visszatérési értéke.
     */
    @Override
    public boolean pickUp(Entity e) {
        return e.pickUpItem(this);
    }

    /**
     * A függvény célja, hogy a saját referenciájával tudja meghívni a paraméterként kapott Entity.Entity “dropItem” rá vonatkozó változtatát. (visitor pattern)
     * @param e - Az Entity.Entity, aki lerakja a sört.
     * @return - Az Entity.Entity erre a tárgyra használt drop() függvényének visszatérési értéke.
     */
    @Override
    public boolean drop(Entity e) {
        //hamissal ter vissza, ha eltunik a targy
        boolean dropSuccess = false;
        if(this.getIsActive()){
            e.removeItem(this);
            GameController.getInstance().removeDecayingItem(this);
        }
        else{
            dropSuccess = e.dropItem(this);
        }
        return dropSuccess;
    }

    /**
     * Ez a függvény a tárgy korának állításáért felel.
     */
    @Override
    public void age() {
        if (isActive){
            setDurability(getDurability() - 1);
        }
    }

    /**
     * Megpróbálja megmenteni a diák életét(megis menti ha aktív és van benne töltet).
     * @param ent Megmentendő diák
     * @return Ha sikerül "true" értékkel tér vissza, ellenkező esetben "false"-al
     */
    @Override
    public boolean save(Entity ent){
        if (isActive && getDurability() > 0){
            ent.saveDrop();
            return true;
        }
        return false;
    }

    /**
     * Visszatér egy Stringgel ami a maga nevéből és az azonosítójából áll
     * @return - A String
     */
    public String toString(){
        return "Item.Beer: #" + Integer.toString(getID());
    }
    @Override
    /**
     * Debug szöveg generálása
     * @param cmdInput - ezzel tud ID-t ellenőrizni
     * @return - Az objektum állapotának szöveges reprezentációja
     */
    public String debug() {
        return "---- Item.Beer " + this.getID() + " ----\ndurability : " + getDurability() + "\nisActive : " + isActive + "\n---- Item.Beer " + this.getID() + " ----\n";
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