package Views;

import GameLogic.GameController;
import Interfaces.*;
import Item.*;
import ViewModels.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class InventoryView extends JPanel implements Observer {
    /**
     * A fő instance, ez az egy van belőle
     */
    private static InventoryView instance;

    /**
     * Az Inventory-t kijelző JPanel
     */
    private JPanel invPanel;

    /**
     * A szoba tárgyait kijelző JPanel
     */
    private JPanel rInvPanel;

    /**
     * Az entityket kijelző JPanel
     */
    private JPanel entityPanel;

    /**
     * A gombokat tartalmazó JPanel
     */
    private JPanel buttonPanel;

    /**
     * Az inventory kijelzéséhez használt metaadatokat tartalmazó panel
     */
    private JPanel metadataPanel1;

    /**
     * A szoba tárgyainak kijelzéséhez használt metaadatokat tartalmazó panel
     */
    private JPanel metadataPanel2;

    /**
     * Az entityk listázásához tartozó metaadatokat tartalmazó panel
     */
    private JPanel entityMetadataPanel;

    /**
     * Az Inventory menedzseléséhez használt listamodell
     */
    public DefaultListModel<String> inventoryListModel;

    /**
     * Az Inventory lista kijelzéséhez használt JList
     */
    private JList<String> inventoryList;

    /**
     * A szobák tárgyainak kijelzéshez használt listamodell
     */
    public DefaultListModel<String> roomInventoryListModel;

    /**
     * A szobák tárgyainak kijelezéséhez használt JList
     */
    private JList<String> roomInventoryList;

    /**
     * Az Entityk kijelzéséhez használt listamodell
     */
    public DefaultListModel<String> entityListModel;

    /**
     * Az Entityk kijelzéséhez használt JList
     */
    public JList<String> entityList;

    /**
     * A "Use" gombhoz kötött JComboBox
     */
    public JComboBox useComboBox;

    /**
     * A "Drop" gombhoz kötött JComboBox
     */
    public JComboBox dropComboBox;

    /**
     * A "PickUp" gombhoz kötött JComboBox
     */
    public JComboBox pickUpComboBox;

    /**
     * A "Connect" gombhoz kötött első JComboBox
     */
    public JComboBox connectComboBox1;

    /**
     * A "Connect" gombhoz kötött második JComboBox
     */
    public JComboBox connectComboBox2;

    /**
     * A "Use" gomb
     */
    private JButton useButton;

    /**
     * A "Drop" gomb
     */
    private JButton dropButton;

    /**
     * A "PickUp" gomb
     */
    private JButton pickUpButton;

    /**
     * A "Connect" gomb
     */
    private JButton connectButton;

    /**
     * A "Disconnect" gomb
     */
    private JButton disconnectButton;

    /**
     * A hozzá tartoző InventoryViewModel referencia
     */
    private InventoryViewModel viewModel;


    /*--------------------------Methods----------------------------------*/


    /**
     * Konstruktor, meghívja az initComponents() függvényt
     */
    public InventoryView(){
        initComponents();
    }

    /**
     * A fő kép elkészítéséért felelős függvény, mindent beállít a kívánt értékekre
     */
    public void initComponents() {
        //Hozzá tartozó viewModel beállítása
        this.viewModel = InventoryViewModel.getInstance();
        viewModel.setInventoryView(this);

        //Layout, és dimenziók állítása
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(200, 512));

        //Az első metaadat panel
        metadataPanel1 = new JPanel(new GridLayout(1, 3));
        metadataPanel1.add(new JLabel("Name"));
        metadataPanel1.add(new JLabel("ID"));
        metadataPanel1.add(new JLabel("Durability"));

        //A második metaadat panel
        metadataPanel2 = new JPanel(new GridLayout(1, 3));
        metadataPanel2.add(new JLabel("Name"));
        metadataPanel2.add(new JLabel("ID"));
        metadataPanel2.add(new JLabel("Durability"));

        //Entity metaadatok
        entityMetadataPanel = new JPanel(new GridLayout(1,3));
        entityMetadataPanel.setPreferredSize(new Dimension(200, 15));
        entityMetadataPanel.add(new JLabel("Name"));
        entityMetadataPanel.add(new JLabel("ID"));
        entityMetadataPanel.add(new JLabel("Stun"));

        //Inventory lista
        inventoryListModel = new DefaultListModel<>();
        inventoryList = new JList<>(inventoryListModel);
        inventoryList.setCellRenderer(new InventoryViewModel.InventoryCellRenderer());

        //Szoba tárgyak lista
        roomInventoryListModel = new DefaultListModel<>();
        roomInventoryList = new JList<>(roomInventoryListModel);
        roomInventoryList.setCellRenderer(new InventoryViewModel.InventoryCellRenderer());

        //Entity lista model
        entityListModel = new DefaultListModel<>();
        entityList = new JList<>(entityListModel);
        entityList.setCellRenderer(new InventoryViewModel.EntityCellRenderer());

        //Inventory panel
        invPanel = new JPanel();
        invPanel.add(metadataPanel1);
        invPanel.add(inventoryList);

        //Szoba inventory panel
        rInvPanel = new JPanel();
        rInvPanel.add(metadataPanel2);
        rInvPanel.add(new JScrollPane(roomInventoryList));

        entityPanel = new JPanel();
        entityPanel.add(entityMetadataPanel);
        entityPanel.add(new JScrollPane(entityList));

        //TabbedPane amiben a fentebbi panelek vannak
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Inv.", invPanel);
        tabbedPane.addTab("Room It.", rInvPanel);
        tabbedPane.addTab("Ent.", entityPanel);
        add(tabbedPane, BorderLayout.CENTER);

        //Gombok panelje, GridBagConstraints-el megoldva
        buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(2,0,2,0);
        useButton = new JButton("Use");
        dropButton = new JButton("Drop");
        pickUpButton = new JButton("PickUp");
        connectButton = new JButton("Connect");
        disconnectButton = new JButton("Disconnect");

        //Comboboxok init
        useComboBox = new JComboBox<>();
        dropComboBox = new JComboBox<>();
        pickUpComboBox = new JComboBox<>();
        connectComboBox1 = new JComboBox<>();
        connectComboBox2 = new JComboBox<>();

        gbc.gridy++;
        buttonPanel.add(new JLabel("Use:"), gbc);
        gbc.gridy++;
        buttonPanel.add(useButton, gbc);
        gbc.gridy++;
        buttonPanel.add(useComboBox, gbc);
        gbc.gridy++;

        buttonPanel.add(new JLabel("Drop:"), gbc);
        gbc.gridy++;
        buttonPanel.add(dropButton, gbc);
        gbc.gridy++;
        buttonPanel.add(dropComboBox, gbc);
        gbc.gridy++;

        buttonPanel.add(new JLabel("Pick Up:"), gbc);
        gbc.gridy++;
        buttonPanel.add(pickUpButton, gbc);
        gbc.gridy++;
        buttonPanel.add(pickUpComboBox, gbc);
        gbc.gridy++;

        buttonPanel.add(new JLabel("Connect:"), gbc);
        gbc.gridy++;
        JPanel connectPanel = new JPanel(new GridLayout(1, 2));
        connectPanel.add(connectButton);
        connectPanel.add(disconnectButton);
        buttonPanel.add(connectPanel, gbc);
        gbc.gridy++;
        buttonPanel.add(connectComboBox1, gbc);
        gbc.gridy++;
        buttonPanel.add(connectComboBox2, gbc);

        add(buttonPanel, BorderLayout.SOUTH);

        //ActionListenerek beállítása, update() hívás
        viewModel.addActionListeners();
        //this.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        update();
    }

    /**
     * Az egyetlen instance elérési módja
     * @return - az instance
     */
    public static synchronized InventoryView getInstance() {
        if (instance == null) {
            instance = new InventoryView();
        }
        return instance;
    }

    /**
     * Meghívja a viewModel "updateInventory()" metódusát
     */
    public void updateInventory() {
        viewModel.updateInventory();
    }

    /**
     * A legfontosabb, update függvény felüldefiniálása, meghívja az "updateInventory()" függvényt
     */
    @Override
    public void update() {
        updateInventory();
    }

    /**
     * Beállítja a "Use" gomb listenerét
     * @param listener - a listener
     */
    public void setUseButtonListener(ActionListener listener) {
        useButton.addActionListener(listener);
    }

    /**
     * Beállítja a "Drop" gomb listenerét
     * @param listener - a listener
     */
    public void setDropButtonListener(ActionListener listener) {
        dropButton.addActionListener(listener);
    }

    /**
     * Beállítja a "PickUp" gomb listenerét
     * @param listener - a listener
     */
    public void setPickUpButtonListener(ActionListener listener) {
        pickUpButton.addActionListener(listener);
    }

    /**
     * Beállítja a "Connect" gomb listenerét
     * @param listener - a listener
     */
    public void setConnectButtonListener(ActionListener listener){
        connectButton.addActionListener(listener);
    }

    /**
     * Beállítja a "Disconnect" gomb listenerét
     * @param listener - a listener
     */
    public void setDisconnectButtonListener(ActionListener listener){
        disconnectButton.addActionListener(listener);
    }

    /**
     * Visszatér a "useComboBox"-ban kiválasztott tárgy String változatával
     * @return - a String
     */
    public String getSelectedUseItem() {
        String value = String.valueOf(useComboBox.getSelectedItem());
        if(!value.isEmpty())
            return value;
        return "";
    }

    /**
     * Visszatér a "dropComboBox"-ban kiválasztott tárgy String változatával
     * @return - a String
     */
    public String getSelectedDropItem() {
        String value = String.valueOf(dropComboBox.getSelectedItem());
        if(!value.isEmpty())
            return value;
        return "";
    }

    /**
     * Visszatér a "pickUpComboBox"-ban kiválasztott tárgy String változatával
     * @return - a String
     */
    public String getSelectedPickUpItem() {
        String value = String.valueOf(pickUpComboBox.getSelectedItem());
        if(!value.isEmpty())
            return value;
        return "";
    }

    /**
     * Visszatér a "connectComboBox1"-ben kiválasztott tárgy String változatával
     * @return - a String
     */
    public String getSelectedConnectItem1() {
        String value = String.valueOf(connectComboBox1.getSelectedItem());
        if(!value.isEmpty())
            return value;
        return "";
    }

    /**
     * Visszatér a "connectComboBox2"-ben kiválasztott tárgy String változatával
     * @return - a String
     */
    public String getSelectedConnectItem2() {
        String value = String.valueOf(connectComboBox2.getSelectedItem());
        if(!value.isEmpty())
            return value;
        return "";
    }
}

