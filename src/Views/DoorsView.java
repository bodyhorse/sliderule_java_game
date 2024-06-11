package Views;

import GameMap.Room;
import Entity.Entity;
import Item.*;
import ViewModels.DoorsViewModel;

import javax.swing.*;

import GameLogic.GameController;

import java.awt.*;
import java.util.ArrayList;

public class DoorsView extends JPanel{

    /**
     * ComboBox amely tartalmazza hogy milyen ajtókon közlekedhet a játékos
     */
    private JComboBox doorList;

    /**
     * Választársra használt gomb
     */
    private JButton chooseButton;

    /**
     * String lista az ajtók formázott nevével
     */
    private ArrayList<String> doors = new ArrayList<>();

    /**
     * A szoba amelynek ajtajit ábrázolni kell
     */
    private Room currentRoom;

    /**
     * Az diák akinek éppen a köre van
     */
    private Entity currentStudent;

    /**
     * Konstruktor
     * @param currRoom A szoba amelynek ajtajit ábrázolni kell
     */
    public DoorsView(Room currRoom) {
        doorList = new JComboBox<>();
        currentRoom = currRoom;
        refreshDoors();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Set vertical BoxLayout

        chooseButton = new JButton("Choose door");

        Box verticalBox = Box.createVerticalBox();
        verticalBox.add(Box.createVerticalGlue());
        verticalBox.add(chooseButton);
        DoorsViewModel VM = new DoorsViewModel(this);
        verticalBox.add(Box.createVerticalGlue());
        verticalBox.add(doorList);


        doorList.setAlignmentX(Component.CENTER_ALIGNMENT);
        chooseButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        this.add(verticalBox);
    }

    /**
     * currentRoom setter függvénye
     * @param newR beállítandó szoba
     */
    public void setCurrentRoom(Room newR){
        currentRoom = newR;
        refreshDoors();
    }

    /**
     * currentRoom getter függvénye
     * @return currentRoom
     */
    public Room getCurrentRoom(){
        return currentRoom;
    }

    /**
     * chooseButton getter függvénye
     * @return chooseButton
     */
    public JButton getChooseButton(){ return chooseButton; }

    /**
     * doorList getter függvénye
     * @return doorList
     */
    public JComboBox getDoorList(){ return doorList; }

    /**
     * Lefrissíti az ajtók listáját a panelen,
     * amennyiben nem üres előtte kis is törli ezután hozzá adja amit a szobában talál.
     */
    public void refreshDoors(){
        //refreshing model
        currentStudent = GameController.getInstance().getCurrentEntity();
    	currentRoom = GameController.getInstance().getCurrentEntity().getCurrentRoom();
        int usableTransistor = studCanTeleport();

        //refreshing string list
        if(!doors.isEmpty()) doors.clear();
        doors.add("Stay");
        for (int i = 0; i < currentRoom.getDoors().size(); i++) {
            doors.add("Door #" + currentRoom.getDoors().get(i).getID());
        }
        if(usableTransistor != -1) doors.add("Transistor #" + usableTransistor);

        //refreshing comboBox
        DefaultComboBoxModel<String> tempModel = new DefaultComboBoxModel<>(doors.toArray(new String[0]));
        doorList.setModel(tempModel);
    }

    /**
     * Eldönti, hogy tud-e a diák teleportálni
     * @return Amennyiben a diáknál összekapcsolt, használt tranzisztor van vissza adja az ID-ját, ellenkező esetben -1-et
     */
    private int studCanTeleport(){
        for(Item it : currentStudent.getInventory()){
            if(it.getPair() != null){
               if(it.getPair().getPair().getTeleport() != "") return it.getID();
            }
        }
        return -1;
    }
}
