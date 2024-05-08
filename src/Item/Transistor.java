package Item;

import Interfaces.Debuggable;

import java.io.Serializable;
import java.util.Objects;

import javax.swing.ImageIcon;

import GameMap.*;
import Entity.*;
import GameLogic.*;


public class Transistor extends Item implements Debuggable, Serializable {
    /**
     * A tranzisztor összekapcsolás során szerzett párja
     */
    private Transistor pair;

    /**
     * Ha a tranzisztor levan helyezve, a szoba referenciája ahol lehejezték
     */
    private Room isPlaced;

    /**
     * A tranzisztort használó Entity.Entity
     */
    private Entity owner;


    /**
     * Az osztály konstruktora, beállítja a tárgy ID-ját.
     * @param aID - A tárgy ID-ja.
     */
    public Transistor(int aID) {
        super(aID);
        setDurability(1);
        GameController.getInstance().debuggableObjects.put(aID, this);
        image = new ImageIcon("rsc/transistor.png").getImage();
    }

    /**
     * A pair attribútum gettere
     * @return - A pair attribútum értéke
     */
    @Override
    public Transistor getPair(){


        return pair;
    }

    /**
     * A pair attribútum settere
     * @param t - A beállítandó pair érték
     */
    public void setPair(Transistor t){
        pair = t;
    }

    /**
     * Az isPlaced attribútum gettere
     * @return - Az isPlaced attribútum értéke
     */
    public Room getIsPlaced(){
        return isPlaced;
    }

    /**
     * Az isPlaced attribútum settere
     * @param r - A beállítandó isPlaced érték
     */
    public Room setIsPlaced(Room r){

        isPlaced = r;


        return r;
    }

    /**
     * Az owner attribútum gettere
     * @return - Az owner attribútum értéke
     */
    public Entity getOwner(){
        return owner;
    }

    /**
     * Az owner attribútum settere
     * @param e - A beállítandó owner érték
     */
    public void setOwner(Entity e){
        owner = e;
    }

    /**
     * Ez a függvény egy összekapcsolt tranzisztort helyez el a paraméterben megadott szobában, ezáltal lehetővé teszi a későbbi oda teleportálást. 
     * @param r - A szoba, ahol a tárgyat használó Entity.Entity tartózkodik.
     * @param e - A tárgyat használó Entity.Entity.
     * @return - True, ha sikeres a lerakás, egyéb esetben False
     */
    @Override
    public boolean use(Room r, Entity e){
        if (pair == null || isPlaced != null || pair.getIsPlaced() != null){
            return false;
        }

        setIsPlaced(r);

        e.removeItem(this);
        r.addItem(this);

        return true;
    }

    /**
     * A játékos szobaváltoztatásáért felelős tranzisztorok használata segítségével. 
     * Ellenőrzi, hogy a tranzisztor a diák által használható-e, ha igen lebonyolítja a szobaváltást és igaz értékkel tér vissza. Ellenkező esetben a visszatérési érték hamis.
     * @param e - Az Entity.Entity, aki teleportál.
     * @return - True, ha sikeres a szobaváltás, egyéb esetben False.
     */
    @Override
    public boolean teleport(Entity e, Room r){
        if (pair == null || e != this.owner){
            return false;
        }
        else if (this.getIsPlaced() == null){
            boolean rep = pair.teleportTrough(e);
            if (rep){
                this.use(r,e);
            }
            return rep;
        }
        else{
            return false;
        }
    }

    /**
     * Megnézi hogy levan-e a rakva az adott tranzisztor (ha nem akkor false-al tér vissza), ezután meghívja annak a szobának az acceptEntity függvényét
     * amelyben levan téve majd elindít egy pickUp folyamatot
     * @param ent - Utazást végző Entity.Entity
     * @return teleportálás sikerességének logikai értéke
     */

    public boolean teleportTrough(Entity ent){

        if (this.isPlaced != null){
            isPlaced.acceptEntity(ent);
            this.pickUp(ent);


            return true;
        }
        else{

            return false;
        }
    }

    /**
     * A függvény célja, hogy a saját referenciájával tudja meghívni a paraméterként kapott Entity.Entity “pickUpItem” rá vonatkozó változtatát. (visitor pattern)
     * @param e - Az Entity.Entity, aki felveszi a Tranzisztort.
     * @return - Az Entity.Entity erre a tárgyra használt pickUpItem függvényének visszatérési értéke.
     */
    @Override
    public boolean pickUp(Entity e) {

        if (owner != null && !owner.equals(e)){
        	GameController.getInstance().errorMsg(210, Integer.toString(getID()));
            return false;
        }
        else{
            isPlaced = null;
            return e.pickUpItem(this);
        }
    }

    /**
     * A függvény célja, hogy a saját referenciájával tudja meghívni a paraméterként kapott Entity.Entity “dropItem” rá vonatkozó változtatát. (visitor pattern)
     * @param e - Az Entity.Entity, aki lerakja a Tranzisztort.
     * @return - Az Entity.Entity erre a tárgyra használt drop() függvényének visszatérési értéke.
     */
    @Override
    public boolean drop(Entity e) {

        if (getPair() != null){

            return false;
        }


        return e.dropItem(this);
    }

    /**
     * Visszatér egy Stringgel, az azonosítójával kiegészítve
     * @return - A String
     */
    public String toString(){
        return "Tranzistor: #" + Integer.toString(getID());
    }

    /**
     * Visszatér azzal, hogy ha a párja le van helyezve valahol, akkor az hol van. Ha nincsen lehelyezve vagy összekapcsolva egy üres Stringet ad vissza
     * @return - a string
     */
    public String getTeleport(){
        if(pair != null && pair.isPlaced != null)
            return pair.isPlaced.toString();
        return "";
    }
    @Override
    /**
     * Debug szöveg generálása
     * @return - Az objektum állapotának szöveges reprezentációja
     */
    public String debug() {
        String pairID;
        if(Objects.isNull(pair)){
            pairID = "No pair";
        }
        else{
            pairID = Integer.toString(pair.getID());
        }
        return "---- Item.Transistor " + this.getID() + " ----\ndurability : " + getDurability() + "\npair : " + pairID +"\n---- Item.Transistor " + this.getID() + " ----\n";
    }

    /**
     * TDA érdekében ez a függvény hívódik meg, ha a paraméter egy Item.Item listából kerül ki.
     * @return - A művelet sikeressége.
     */
    @Override
    public boolean connect(Item i, Entity ent) {       
        return i.TransistorConnect(this, ent);
    }

    /**
     * TDA érdekében ez a függvény hívódik meg, ha a paraméter egy Item.Item listából kerül ki.
     * @return - A művelet sikeressége.
     */
    @Override
    public boolean disconnect(Item i) {
        return i.TransistorDisconnect(this);
    }

    /**
     * Connectet végző függvény.
     */
    @Override
    public boolean TransistorConnect(Transistor t, Entity e) {
        if (pair != null){
            return false;
        }
        this.owner = e;
        pair = t;
        t.TransistorConnect(this, e);

        return true;
    }

    /**
     * Disconnectet végző függvény.
     */
    @Override
    public boolean TransistorDisconnect(Transistor t) {
        if (t == null || pair == null){
            return false;
        }

        if (!(pair.equals(t))){
            String temp = "" + this.getID() + ", " + t.getID();
            GameController.getInstance().errorMsg(503, temp);
            return false;
        }

        pair = null;
        owner = null;
        t.TransistorDisconnect(this);

        return true;
    }
}
