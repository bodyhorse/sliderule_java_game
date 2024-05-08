package Entity;

 import Interfaces.Debuggable;

 import java.awt.*;
 import java.io.Serializable;
 import java.util.*;
 import java.util.List;

 import GameMap.*;
 import GameLogic.*;
 import Interfaces.Observable;
 import Interfaces.Observer;
 import Item.*;


public abstract class Entity extends Observable implements Serializable , Debuggable, Observer {
    /**
	 * Default serial version ID
	 */
	private static final long serialVersionUID = 1L;
	/**
     * Entity.Entity neve
     */
    protected String name;
    /**
     * Entity.Entity egyedi azonosítója
     */
    protected int ID;
    /**
     * Számon tartja, hogy az Entity.Entity hány körből marad még ki
     */
    protected int remainingStun;
    /**
     * A szoba ahol az Entity.Entity jelenleg tartózkodik
     */
    protected Room currentRoom;
    /**
     * Entity.Entity lélekkel rendelkezésének logikai értéke
     */
    protected boolean isAlive;
    /**
     * Az Entity.Entity gázzal telített szobában tartozkodásának logikai értéke
     */
    protected boolean isInToxic;
    /**
     * Az Entity.Entity hátizsákja
     */
    protected List<Item> inventory;
    
    /**
     * Logikai értéke annak, hogy az Entity.Entity saját köre befejeződött-e
     */
    protected boolean roundIsOver;

    protected Image image;

    /**
     * Konstruktor
     * @param name - Entity.Entity neve
     * @param id - Entity.Entity egyedig ID-je
     * @param currentRoom - A szoba amelyben az Entity.Entity tartózkodik
     */
    protected Entity(String name, int id, Room currentRoom) {
        this.name = name;
        this.ID = id;
        this.remainingStun = 0;
        setCurrentRoom(currentRoom);
        this.isAlive = true;
        this.isInToxic = false;
        this.inventory = new ArrayList<Item>();
        this.roundIsOver = false;
    }

    //Getters - setters for a while here :)

    /**
     * Name getter függvény
     * @return - Az entity neve
     */
    public String getName() {
        return this.name;
    }

    /**
     * ID getter függvény
     * @return - Az Entity.Entity ID-je
     */
    public int getID(){
        return this.ID;
    }
    
    /**
     * RemainingStun getter függvény
     * @return - Körök száma ameddig az entity kimarad a játékból
     */
    public int getRemainingStun() {
    	return this.remainingStun;
    }

    /**
     * CurrentRoom getter függvény
     * @return - A szoba, melyben az Entity.Entity épp van
     */
    public Room getCurrentRoom(){
        return this.currentRoom;
    }

    /**
     * IsAlive getter függvény
     * @return - True ha az entity él, False hogyha halott
     */
    public boolean getIsAlive(){
	
        return this.isAlive;
    }

    /**
     * IsInToxic getter függvény
     * @return - True ha mérgező szobában van, False hogyha nincs
     */
    public boolean getIsInToxic(){
        return this.isInToxic;
    }

    /**
     * Inventory getter függvény
     * @return - Az Entity.Entity inventory-a
     */
    public List<Item> getInventory(){
        return this.inventory;
    }

    /**
     * Name setter
     * @param name - Az Entity.Entity neve
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * ID setter
     * @param id - Az Entity.Entity ID-je
     */
    public void setID(int id){
        this.ID = id;
    }
    
    /**
     * RemainingStun setter
     * @param stun Körök száma ameddig az entity kimarad a játékból
     */
    public void setRemainingStun(int stun) {
    	this.remainingStun = stun;
    }

    /**
     * CurrentRoom setter
     * @param currentRoom - A szoba amiben az Entity.Entity van
     */
    public void setCurrentRoom(Room currentRoom) {
        removeObserver(this.currentRoom);
        this.currentRoom = currentRoom;
        addObserver(currentRoom);
    }

    /**
     * IsAlive setter
     * @param isAlive - True hogyha az Entity.Entity él, False hogyha nem
     */
    public void setIsAlive(boolean isAlive){
        this.isAlive = isAlive;
    }

    /**
     * IsInToxic setter
     * @param isInToxic - True, hogyha mérgező szobában van, False hogyha nem
     */
    public void setIsInToxic(boolean isInToxic){
        this.isInToxic = isInToxic;
    }

    /**
     * Inventory setter
     * @param inventory - Az Entity.Entity inventory-ja
     */
    public void setInventory(List<Item> inventory){
        this.inventory = inventory;
    }

    /**
     * Item.SlideRule tárgy felvételére szolgáló függvény.
     * Absztrakt, mivel a Studetn és a Entity.Teacher külön módon kezeli.
     * @param slideRule - A tárgy amit fel akar venni
     * @return - True ha fel tudja venni, False hogyha nem
     */
    public abstract boolean pickUpItem(SlideRule slideRule);

    /**
     * Tranzisztor tárgy felvételére szolgáló tárgy
     * Absztrakt, mivel a Entity.Student és a Entity.Teacher külön módon kezeli
     * @param transistor - A tárgy amit fel akar venni.
     * @return - True hogyha fel tudja venni, False hogyha nem
     */
    public abstract boolean pickUpItem(Transistor transistor);


    /**
     * Rongy tárgy felvétele. Ellenőrzi, hogy belefér-e az inventory-ba. Utána ellenőrzi, hogy már aktív-e a rongy.
     * Hogyha nem aktív, akkor aktiváljuk, és hozzáadjuk a GameMap.Map decaying tárgyaihoz.
     * Hogyha aktív, akkor csak szimplán felvesszük.
     * @param rag - A rongy amit fel akarunk venni
     * @return - True ha fel tudtuk venni, false, hogyha nem
     */
    public boolean pickUpItem(Rag rag){

        if(!canPickUp(rag.getID())) {
            return false;
        }

        //Remove from room + add to inv
        Item it = currentRoom.removeItem(rag);
        if(it == null) return false;

        this.addItem((Rag)it);
        GameController.getInstance().addDecayingItem((Rag)it);
        ((Rag) it).setIsActive(true);

        return true;
    }

    /**
     * A sör tárgy felvételét kezelő függvény. Ellenőrzi, hogy belefér-e az inventory-ba.
     * @param beer sör amit felveszünk
     * @return fevétel sikerességének logikai értéke
     */
    public boolean pickUpItem(Beer beer){
        if(!canPickUp(beer.getID())) {
            return false;
        }
        
        //inventory size check
        int curInvSize = getInventory().size();
        //if the inventory is full, therefore the entity cant pick up an item
        if(curInvSize >= 5) return false;

        //Remove from room
        Item beerRemoved = currentRoom.removeItem(beer);
        //if the beer was successfully removed from the room, it can be added to the entity's inventory
        if (beerRemoved != null){
            inventory.add(0, (Beer)beerRemoved);
            return true;
        }
        return false;
    }

    /**
     * Általános tárgyfelvétel logika. Hogyha Item.Beer-t vesz fel, akkor az az inventory legelejére kerül, minden más pedig a végére
     * @param item - A tárgy amit fel szeretnénk venni
     * @return - True, ha belefér az inventoryba, False hogyha pedig nem fér bele
     */
    public boolean pickUpItem(Item item){

        if(!canPickUp(item.getID())) {
            return false;
        }


        //Remove from room
        Item it = currentRoom.removeItem(item);
        if (it != null){
            addItem(it);

            return true;
        }
        return false;
    }
    
 
    /**
     * Egy Entity.Entity és egy diák találkozását kezelő virtuális függvény.
     * Mivel a találkozásnak az entityből leszármazott osztály típusától függően
     * különböző műveletei vannak, így ott felül kell definiálni.
     * Ha ez nem történik meg a találkozás művelet nélkül zajlik.
     * @param stdnt A diák referenciája akivel az Entity.Entity találkozott.
     */
    public void meet(Student stdnt) {
    	//Üres, hiszen alap esetben a találkozó nem jár következménnyel.
    }
    
    /**
     * Egy Entity.Entity és egy tanár találkozását kezelő virtuális függvény.
     * Mivel a találkozásnak az entityből leszármazott osztály típusától függően
     * Különböző műveletei vannak, így ott felül kell definiálni.
     * Ha ez nem történik meg a találkozás művelet nélkül zajlik.
     * @param tchr A tanár referenciája akivel az Entity.Entity találkozott.
     */
    public void  meet(Teacher tchr) {
    	//Üres, hiszen alap esetben a találkozó nem jár következménnyel.
    }
    
    /**
     * Egy Entity.Entity és egy takarító találkozását kezelő virtuális függvény.
     * Mivel a találkozásnak az entityből leszármazott osztály típusától függően
     * Különböző műveletei vannak, így ott felül kell definiálni.
     * Ha ez nem történik meg a találkozás művelet nélkül zajlik.
     * @param jntr A takarító referenciája akivel az Entity.Entity találkozott.
     */
    public void  meet(Janitor jntr) {
    	//Üres, hiszen alap esetben a találkozó nem jár következménnyel.
    }
    
    /**
     * Két Entity.Entity találkozását kezelő függvény. Célja, hogy
     * a találkozásnak megfelelő függvény hívódjon a származtatott osztályokban. (Visitor)
     * Ehhez a paraméterül kapott entity-re meghívja a saját maga referenciájával a meet 
     * metódust.
     * @param entity Az entity referenciája akivel a találkozás történik.
     */
    public abstract void meetMe(Entity entity);
    
    /**
     * Kezeli az Entity.Entity pozícióváltását a szobák között ajtókon keresztül. Meghívja
     *a paraméterül adott ajtó moveEntity() függvényét, és annak visszatérési értékével tér vissza.
     * @param door Ajtó, amelyen keresztül az entity szobát változtat.
     * @return A szobaváltás sikerességének logikai értéke
     */
    public boolean moveTo(Door door){
    	
    	//ha a kapott ajtó referencia null, helyben akar maradni az Entity.Entity
    	if(door == null) {
    		roundIsOver = currentRoom.acceptEntity(this);
    	}else {
    		if(remainingStun == 0) {
        		boolean ret = door.moveEntity(this, currentRoom); 
        		
        		//Ha sikerül a szobaváltoztatás az Entity.Entity körének vége
        		roundIsOver = ret;
        	}else {
            	return false;
        	}
    	} 
    	return roundIsOver;
     }

    /**
     * Sör tárgy eldobás logikája. Hogyha aktiválva van a tárgy, akkor eldobásra el kell hogy tűnjön az inventory-ból.
     * Ha nincs aktiválva megjelenik a szoba inventoryában
     * @param beer - A tárgy amit el szeretnénk dobni
     * @return - True hogyha el tudjuk dobni, False hogyha nem
     */
    public boolean dropItem(Beer beer){

        beer = (Beer) removeItem(beer);

        this.currentRoom.addItem(beer);

        return true;
    }

    /**
     * Item.Camembert tárgy eldobása
     * @param item - A tárgy amit el szeretnénk dobni
     * @return - True ha el tudjuk dobni, False hogyha nem
     */
    public boolean dropItem(Item item){
        this.removeItem(item);
        this.currentRoom.addItem(item);
        
        return true;
    }

    /**
     * Item.Mask tárgy eldobási logika. Gázos szobában nem lehet eldobni maszkot, ilyenkor False-al térünk vissza. Hogyha el lehet
     * dobni, akkor kikerül az Entity.Entity inventoryából, és belekerül a szoba inventoryába
     * @param mask - A tárgy amit el szeretne dobni
     * @return - True ha el tudja dobni, False hogyha nem
     */
    public boolean dropItem(Mask mask){
        if(isInToxic){
        	GameController.getInstance().errorMsg(205, Integer.toString(mask.getID()));
            return false;
        }
        else{
            mask = (Mask) removeItem(mask);
            this.currentRoom.addItem(mask);
        }
        return true;
    }

    /**
     * Item.Rag tárgy eldobási logikája. Mindig el tudjuk dobni. A tárgy kikerül az Entity.Entity inventoryából, és belekerül a szoba inventoryába
     * @param rag - A tárgy amit el szeretnénk dobni
     * @return - True, hogyha el tudtuk dobni, False hogyha nem
     */
    public boolean dropItem(Rag rag){
        rag = (Rag) removeItem(rag);
        this.currentRoom.addItem(rag);
        return true;
    }

    /**
     * Item.SlideRule eldobási logikája. A tárgy kikerül az Entity.Entity inventoryából, és belekerlül a szoba inventoryába.
     * @param slideRule - A tárgy amit el szeretnénk dobni.
     * @return - True, hogyha el tudjuk dobni, False hogyha nem tudjuk eldobni.
     */
    public boolean dropItem(SlideRule slideRule){
        slideRule = (SlideRule) removeItem(slideRule);
        this.currentRoom.addItem(slideRule);
        return true;
    }

    /**
     * Item.Transistor tárgy eldobási logika. Ha párba van állítva nem dobhatjuk el.
     * @param transistor - A tárgy amit el akarunk dobni
     * @return - True hogyha el tudtuk dobni, False hogyha nem
     */
    public boolean dropItem(Transistor transistor){
        transistor = (Transistor) removeItem(transistor);
        this.currentRoom.addItem(transistor);
        return true;
    }
    
    /**
     * Entity.Entity egy gázzal telített szobába lépésével járó állapotváltozásait kezeli.
     * Ehhez az összes hátizsákjában lévő tárgyon meghívja a „preventGasStun()” metódust
     * egészen addig ameddig az első „true” értékkel nem tér vissza, ekkor további
     * művelet nélkül befejeződik a metódus futása. Ha egyetlen ilyen tárgy sincs akkor beállítja
     * a „remainingStun” értékét 3-ra, valamint az Entity.Entity hátizsákjából az összes tárgyat eldobja a “dropInventory()” meghívásával.
     */
    public void toxicate(){
    	setIsInToxic(true);
    	
    	for(Item item : inventory) {
    		boolean res = item.preventGasStun();
    		if(res) return;
    	}
    	
    	dropInventory();
    	setRemainingStun(3);
    }

    /**
     * Entity.Entity egy normál szobába belépésével járó állapotváltozásokat kezeli.
     * Beállítja az „isInToxic” tagváltozó értékét hamisra, valamint az összes
     * hátizsákjában lévő tárgyra meghívja a „carriedIntoNormalRoom()” metódust.
     */
    public void enteredNormal(){   	
    	setIsInToxic(false);
    	for(Item item : inventory) {
    		item.carriedIntoNormalRoom();
    	}
    }

    /**
     *  Absztrakt metódus, amely kezeli a fordulón belül az adott Entity.Entity cselekvéseit.
     * @return - Az Entity.Entity kör végén való életben maradásának logika értéke
     */
    public abstract boolean newEntRound();

    /**
     * Az Entity.Entity jelenlegi pozíciójához tartozó szobából eltávolítja magát annak
     * removeEntity() metódusának meghívásával. Ezt követően a pozícióját nyilvántartó
     * currentRoom változó értéket a paraméterben kapott szoba értékére változtatja.
     * @param room Szoba ahová az Entity.Entity pozícióját változtatta.
     * @return A szobaváltás sikerességének logikai értéke.
     */
    public boolean roomChanged(Room room){
    	currentRoom.removeEntity(this);
    	setCurrentRoom(room);
        return true;
    }

    /**
     * Egy Entity.Entity rongyal találkozását kezelő metódus.
     */
    public void encounterRag(){}

    /**
     * Hozzáad egy tárgyat az inventoryhoz. Amikor hozzáadja, mindig az utolsó helyre teszi, a Item.Beer kezelése miatt
     * @param item - A tárgy, amit hozzá kell adni az invenotryhoz
     */
    public void addItem(Item item){
        inventory.add(item);
        item.addObserver(this);
    }

    /**
     * Elvesz egy tárgyat az inventoryból.
     * @param item - A tárgy, amit ki kell venni az inventoryból.
     * @return - A tárgy amit éppen kivett az invenotryból.
     */
    public Item removeItem(Item item){
        inventory.remove(item);
        item.removeObserver(this);
        return item;
    }

    /**
     * Eldobja az összes tárgyat az inventoryból, és beleteszi a 'currentRoom' inventoryjába.
     * Meghívja az inventoryban kévő összes tárgyra a dropItem()-et.
     * Hogyha valamelyik false-al tér vissza, akkor exceptiont dob.
     */
    public void dropInventory(){
    	
        //Iterate on all items, call dropItem on each one
        List<Item> help = new ArrayList<>(inventory);
        for(Item item : help){
            if(!item.drop(this))
                GameController.getInstance().errorMsg(205, item.toString());
        }
    }

    /**
     * Inventoryba való felvehetőség kiszervezése külön függvénybe
     * @param itemId A kérdéses tárgy azonosítója, amit fel szeretnénk venni
     * @return - True hogyha belefér az invenotryba, False hogyha nem
     */
    public boolean canPickUp(Integer itemId){
    	boolean canPickUp = (inventory.size() <= 4);
    	if(!canPickUp) GameController.getInstance().errorMsg(202, itemId.toString());
    	return canPickUp;
    }

    /**
     * Virtuális metódus annak támogatására, hogy ha egy Entity.Entity sörrel mentette meg a lelkét eldobja egy a hátizsákjában található tárgyat.
     * Mivel nem minden Entityre vonatkozik, ezért a törzse üres. Ahol ez funkcionalitással rendelkezik felül kell definiálni.
     */
    public void saveDrop(){}


    /**
     * Függvény az entity inventoryjának kilistázására TDA helyesen
     * @return - A lista
     */
    public String listItems(){
        String message = "";
        String section;
        for (Item item : inventory) {
            //Minden item toString()-je felülírva
            section = item.toString();
            message += "\t-" + section + "\n";
        }
        return message;
    }

    /**
     * Listába szedi az entity teleportálási lehetőségeit. Végignézi, hogy van e tranzisztora, ha van akkor azon meghívja a getTeleport() függvényt.
     * Ha nem üres a String amit az visszaad, akkor hozzáadja a listához. Ha üres nem történik semmi.
     * @return - a kész lista
     */
    public String listTeleport(){
        String message = "";
        String section = "";
        for (Item item : inventory) {
            if (item.getClass() == Transistor.class) {
                Transistor trans = (Transistor) item;
                section = trans.getTeleport();
            }
            if (section != "") {
                message += "\t-" + section + "\n";
            }
            section = "";
        }
        return message;
    }
    /**
     *  item use parancsának kezelése
     * @param input - A kapott command felosztva szóköz mentén
     * @return - státusz
     */
    public boolean initiateUse(String[] input){
        Item grabbedItem = null;
        int targetItemID = Integer.parseInt(input[1]);
        //használandó tárgy keresése
        for(Item i : inventory){
            if(i.checkID(targetItemID)){
                grabbedItem = i;
                break;
            }
        }
        //ha rossz IDt ad a user
        if(grabbedItem == null){
            GameController.getInstance().errorMsg(208, input[1]);
            return false;
        }
        GameController.getInstance().statusMsg(213, String.valueOf(targetItemID));
        return grabbedItem.use(currentRoom, this);
    }
    /**
     *  item drop parancsának kezelése
     * @param inp - A kapott command felosztva szóköz mentén
     * @return - státusz
     */
    public boolean initiateDrop(String[] inp){
        //ha -a t kap, akkor minden item eldobódik
        if(inp.length == 3 && inp[2].equals("-a")){
            dropInventory();
            return true;
        }

        Item grabbedItem = null;
        int targetItemID = Integer.parseInt(inp[1]);

        //Tárgy keresése inventory-ban
        for(Item i: inventory){

            if(i.checkID(targetItemID)){
                grabbedItem = i;
                break;
            }
        }
        //ha a tárgy nem található a zsákban:
        if(grabbedItem == null){
            GameController.getInstance().errorMsg(204,inp[1]);
            return false;
         }

        boolean ret = grabbedItem.drop(this);
        
        if (ret)
        	GameController.getInstance().statusMsg(212, String.valueOf(targetItemID));
        
        return ret;
    }

    /**
     * 2 item connect parancsának kezelése
     * @param split - A kapott command space-ekkel elválasztva
     * @return
     */
    public boolean initiateConnect(String[] split){
        //wrong syntax
        if (split.length < 3){
            GameController.getInstance().errorMsg(0, "");
            return false;
        }

        Item grabbedItem1 = null;
        Item grabbedItem2 = null;
        int targetItemID1 = Integer.parseInt(split[1]);
        int targetItemID2 = Integer.parseInt(split[2]);

        //Tárgy keresése inventory-ban
        for (int i = 0; i < inventory.size(); i++){
            if (inventory.get(i).checkID(targetItemID1)){
                grabbedItem1 = inventory.get(i);
            }
            else if (inventory.get(i).checkID(targetItemID2)){
                grabbedItem2 = inventory.get(i);
            }
        }
        //ha a tárgy nem található a zsákban:
        if(grabbedItem1 == null || grabbedItem2 == null){
            GameController.getInstance().errorMsg(204,split[1] + ", " + split[2]);
            return false;
         }

         GameController.getInstance().statusMsg(511, targetItemID1 + ", " + targetItemID2);

         return grabbedItem1.connect(grabbedItem2, this);
    }

    /**
     * 2 item disconnect parancsának kezelése
     * @param split - A kapott command space-ekkel elválasztva
     * @return
     */
    public boolean initiateDisconnect(String[] split){
        //wrong syntax
        if (split.length < 3){
            GameController.getInstance().errorMsg(0, "");
            return false;
        }

        Item grabbedItem1 = null;
        int targetItemID1 = Integer.parseInt(split[1]);

        //Tárgy keresése inventory-ban
        for (int i = 0; i < inventory.size(); i++){
            if (inventory.get(i).checkID(targetItemID1)){
                grabbedItem1 = inventory.get(i);
            }
        }
        //ha a tárgy nem található a zsákban:
        if(grabbedItem1 == null){
            GameController.getInstance().errorMsg(204,split[1]);
            return false;
        }

        if (grabbedItem1.getPair() == null){
            return false;
        }

        GameController.getInstance().statusMsg(512, "" + targetItemID1);
        return grabbedItem1.disconnect(grabbedItem1.getPair());
    }

    /**
     * Teleportálás kezelése
     * @param split
     * @return A teleport sikeressége
     */
    public boolean initiateTeleport(String[] split){
        int targetItemID;
        Item grabbedItem = null;

        try{
            targetItemID = Integer.parseInt(split[1]);
        }
        finally{

        }

        for (int i = 0; i < inventory.size(); i++){
            if (inventory.get(i).checkID(targetItemID)){
                grabbedItem = inventory.get(i);
                break;
            }
        }

        if (grabbedItem == null){
            return false;
        }

        boolean b = grabbedItem.teleport(this, this.currentRoom);

        //sikeres a teleport
        if (b){
            this.roundIsOver = true;
            return true;
        }
        
        return false;
    }

    /**
     * Debug szöveg generálása
     * @return - Az objektum állapotának szöveges reprezentációja
     */
    @Override
    public String debug() {
        String retVal =  "---- " + this.getClass().getName() + " "  + this.getID() + " ----\nname : " + name + "\nremainingStun : " + remainingStun +"\ncurrentRoom : " + currentRoom.getID() + "\nisAlive : " + isAlive + "\nisInToxic : " + isInToxic + "\nroundIsOver : " + roundIsOver + "\ninventory :\n" ;
        
        for(Item i: inventory){
            retVal +=  i.debug();
        }
        retVal += "---- " + this.getClass().getName() + " "  + this.getID() + " ----\n";
        
        return retVal;
    }

    /**
     * Default for random name generation funcitons in child classes
     * @return - "randomName"
     */
    public String randomName(){
        return "randomName";
    }


    @Override
    public void update(){
        notifyObservers();
    }

    public Image getImage() {
        return image;
    }
}
