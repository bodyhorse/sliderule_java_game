package ViewModels;

import GameLogic.GameController;
import Interfaces.Observer;
import Views.InventoryView;
import Item.*;
import Entity.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class InventoryViewModel implements Observer {
    /**
     * Jelenlegi játékos
     */
    private Entity current;

    /**
     * Az egyetlen instance
     */
    private static InventoryViewModel instance;

    /**
     * A hozzá tartozó InventoryView
     */
    private InventoryView inventoryView;

    /**
     * A "Use" gomb ActionListenere
     */
    public ActionListener useButtonListener;

    /**
     * A "Drop" gomb ActionListenere
     */
    public ActionListener dropButtonListener;

    /**
     * A "PickUp" gomb ActionListenere
     */
    public ActionListener pickUpButtonListener;

    /**
     * A "Connect" gomb ActionListenere
     */
    public ActionListener connectButtonListener;

    /**
     * A "Disconnect" gomb ActionListenere
     */
    public ActionListener disconnectButtonListener;

    /**
     * Az Inventoryban tárolt tárgyak String listája
     */
    private ArrayList<String> invList = new ArrayList<>();

    /**
     * A szobában tárolt tárgyak String listája
     */
    private ArrayList<String> roomInvList = new ArrayList<>();

    private ArrayList<String> entityList = new ArrayList<>();


    /*-------------------------Methods-----------------------------*/


    /**
     * A konstruktor, ahol az összes actionlistenert elkészítjük
     */
    public InventoryViewModel(){
        //"Use" gomb ActionListenerje, meghívja a GameController "use()" függvényét a helyes paraméterek beállítása után, majd frissít
        useButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(inventoryView.getSelectedUseItem().isEmpty())
                    return;
                try {
                    String[] splitInfo = inventoryView.getSelectedUseItem().strip().split("#");

                    GameController.getInstance().terminal("use " + splitInfo[1]);
                    inventoryView.updateInventory();
                }
                catch (Exception ex){
                    return;
                }
            }
        };

        //"Drop" gomb ActionListenerje, meghívja a GameController "drop()" függvényét a helyes paraméterek beállítása után, majd frissít
        dropButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(inventoryView.getSelectedDropItem().isEmpty())
                    return;
                try{
                    String[] splitInfo = inventoryView.getSelectedDropItem().strip().split("#");

                    GameController.getInstance().terminal("drop " + splitInfo[1]);
                    inventoryView.updateInventory();
                }
                catch (Exception ex){
                    return;
                }
            }
        };

        //"PickUp" gomb ActionListenerje, meghívja a GameController "pickup()" függvényét a helyes paraméterek beállítása után, majd frissít
        pickUpButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(inventoryView.getSelectedPickUpItem().isEmpty())
                    return;
                try{
                    String[] splitInfo = inventoryView.getSelectedPickUpItem().strip().split("#");

                    GameController.getInstance().terminal("pickup " + splitInfo[1]);
                    inventoryView.updateInventory();
                }
                catch (Exception ex){
                    return;
                }
            }
        };

        //"Connect" gomb ActionListenerje, meghívja a GameController "connect()" függvényét a helyes paraméterek beállítása után, majd frissít
        connectButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(inventoryView.getSelectedConnectItem1().isEmpty() || inventoryView.getSelectedConnectItem2().isEmpty())
                    return;
                try{
                    String[] splitInfo1 = inventoryView.getSelectedConnectItem1().strip().split("#");
                    String[] splitInfo2 = inventoryView.getSelectedConnectItem2().strip().split("#");

                    GameController.getInstance().terminal("connect " + splitInfo1[1] + " " + splitInfo2[1]);
                    inventoryView.updateInventory();
                }
                catch (Exception ex){
                    return;
                }

            }
        };

        //A "Disconnect" gomb ActionListenere, meghyvja a GameController "terminal()" függvénylt, hogyha helyesen próbálja használni a felhasználó. Ha nem akkor nem törtéink semmi
        disconnectButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(inventoryView.getSelectedConnectItem1().isEmpty() || inventoryView.getSelectedConnectItem2().isEmpty())
                    return;
                    
                try{
                    String[] splitInfo1 = inventoryView.getSelectedConnectItem1().strip().split("#");
                    String[] splitInfo2 = inventoryView.getSelectedConnectItem2().strip().split("#");
                    Item item1 = GameController.getInstance().getCurrentEntity().itemById(Integer.parseInt(splitInfo1[1]));
                    Item item2 = GameController.getInstance().getCurrentEntity().itemById(Integer.parseInt(splitInfo2[1]));
                    if(item1 == null || item2 == null)
                        return;
                    
                    if(item1.getPair() != item2)
                        return;
                    
                    GameController.getInstance().terminal("disconnect " + splitInfo1[1] + " " + splitInfo2[1]);
                    inventoryView.updateInventory();
                }
                catch(Exception ex){
                    return;
                }
            }
        };
    }

    /**
     * Beállítja a hozzá tartozó InventoryView instance-t
     * @param view - a beállítandó instance
     */
    public void setInventoryView(InventoryView view) {
        this.inventoryView = view;
    }

    /**
     * Visszatér az egyetlen létező instance-el, ha nincs akkor csinál egyet
     * @return - az instance
     */
    public static synchronized InventoryViewModel getInstance() {
        if (instance == null) {
            instance = new InventoryViewModel();
        }
        return instance;
    }

    /**
     * A legfontosabb függvény, az "update()" felüldefiniálása, ha a jelenlegi entitás nem ugyanaz mint az eltárolt, akkor frissít
     */
    @Override
    public void update() {
        if (GameController.getInstance().getCurrentEntity() != current) {
            current = GameController.getInstance().getCurrentEntity();
            updateInventory();
        }
    }

    /**
     * A legfontosabb update függvény, minden változásra frissíti az összes kijelzett adatot
     */
    public void updateInventory() {
        //Elkérjük az aktuális listákat
        java.util.List<Item> invItems = GameController.getInstance().getCurrentEntity().getInventory();
        ArrayList<Item> roomItems = GameController.getInstance().getCurrentEntity().getCurrentRoom().getContainedItems();
        ArrayList<Entity> entities = GameController.getInstance().getCurrentEntity().getCurrentRoom().getEntitesInside();

        //Kitisztítjuk az elemeket
        clearInventory();

        //Beleteszünk mindent a játékos inventoryval foglalkozó dolgokba
        for (int i = 0; i < invItems.size(); i++) {
            Item item = invItems.get(i);
            String itemString = item.toString();
            Object itemObject = makeObj(itemString);

            //Ezekhez mindent hozzáadunk
            invList.add(itemString);
            inventoryView.inventoryListModel.add(i, itemString);
            inventoryView.dropComboBox.addItem(itemObject);

            //Ezekbe csak speciális esetekben adunk hozzá tárgyat
            if(itemString.contains("Transistor")){
                inventoryView.connectComboBox1.addItem(itemObject);
                inventoryView.connectComboBox2.addItem(itemObject);
                if(item.getPair()!= null)
                    inventoryView.useComboBox.addItem(itemObject);
            }
            else if(itemString.contains("AirFreshner") || itemString.contains("Beer") || itemString.contains("Camembert")){
                inventoryView.useComboBox.addItem(itemObject);
            }
        }

        //Beleteszünk mindent a szobával foglalkozó helyekre
        for (int i = 0; i < roomItems.size(); i++) {
            Item item = roomItems.get(i);
            String itemString = item.toString();
            Object itemObject = makeObj(itemString);
            roomInvList.add(itemString);
            inventoryView.pickUpComboBox.addItem(itemObject);
            inventoryView.roomInventoryListModel.add(i, itemString);
        }

        //Beleteszünk mindent az entityvel foglalkozó helyekre
        for(int i = 0; i < entities.size(); i++){
            Entity entity = entities.get(i);
            String entityString = entity.getName() + "#" + entity.getID();
            inventoryView.entityListModel.add(i, entityString);
        }
    }

    /**
     * Kitisztítja az InventoryView-ban eltárolt adatokat
     */
    public void clearInventory() {
        roomInvList.clear();
        inventoryView.roomInventoryListModel.clear();
        inventoryView.pickUpComboBox.removeAllItems();

        invList.clear();
        inventoryView.inventoryListModel.clear();
        inventoryView.useComboBox.removeAllItems();
        inventoryView.dropComboBox.removeAllItems();
        inventoryView.connectComboBox1.removeAllItems();
        inventoryView.connectComboBox2.removeAllItems();

        entityList.clear();
        inventoryView.entityListModel.clear();
    }

    /**
     * Beállítja az InventoryViewban használandó ActionListenereket
     */
    public void addActionListeners() {
        inventoryView.setUseButtonListener(useButtonListener);
        inventoryView.setDropButtonListener(dropButtonListener);
        inventoryView.setPickUpButtonListener(pickUpButtonListener);
        inventoryView.setConnectButtonListener(connectButtonListener);
        inventoryView.setDisconnectButtonListener(disconnectButtonListener);
    }

    /**
     * Az InventoryView-ban használt JList-ekhez tartozó ListCellRenderer<String>
     */
    public static class InventoryCellRenderer extends JPanel implements ListCellRenderer<String> {
        Item item;

        public InventoryCellRenderer() {
            GridLayout layout = new GridLayout(1, 3);
            setPreferredSize(new Dimension(200, 15));
            setLayout(layout);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
            removeAll();
            String[] splitItem = value.strip().split("#");
            try{
                if(GameController.getInstance().getCurrentEntity().itemById(Integer.parseInt(splitItem[1])) != null)
                    item = GameController.getInstance().getCurrentEntity().itemById(Integer.parseInt(splitItem[1]));
                else if(GameController.getInstance().getCurrentEntity().getCurrentRoom().itemById(Integer.parseInt(splitItem[1])) != null)
                    item = GameController.getInstance().getCurrentEntity().getCurrentRoom().itemById(Integer.parseInt(splitItem[1]));
            }
            catch (Exception e){
                return null;
            }
            JLabel nameLabel = new JLabel(  splitItem[0]);
            nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            add(nameLabel);
            add(new JLabel(splitItem[1]));
            add(new JLabel(String.valueOf(item.getDurability())));
            return this;
        }
    }

    public static class EntityCellRenderer extends JPanel implements ListCellRenderer<String> {
        Entity entity;

        public EntityCellRenderer() {
            GridLayout layout = new GridLayout(1, 3);
            setPreferredSize(new Dimension(200, 15));
            setLayout(layout);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
            removeAll();
            String[] splitEntity = value.split("#");
            try{
                if(GameController.getInstance().getCurrentEntity().getCurrentRoom().entityById(Integer.parseInt(splitEntity[1])) != null)
                    entity = GameController.getInstance().getCurrentEntity().getCurrentRoom().entityById(Integer.parseInt(splitEntity[1]));
                else
                    return null;
            } catch (Exception e){
                return null;
            }
            add(new JLabel(splitEntity[0]));
            add(new JLabel(splitEntity[1]));
            add(new JLabel(Integer.toString(entity.getRemainingStun())));
            return this;
        }
    }

    /**
     * Objectet készít a Stringből, a ComboBox mágia miatt
     * @param item - a String
     * @return - az Object
     */
    private Object makeObj(final String item)  {
        return new Object() {
            public String toString() {
                return item;
            }
        };
    }
}
