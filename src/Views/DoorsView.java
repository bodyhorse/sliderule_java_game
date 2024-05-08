package Views;

import GameMap.Room;
import Interfaces.Observer;
import ViewModels.DoorsViewModel;

import javax.swing.*;
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
     * Konstruktor
     * @param currRoom A szoba amelynek ajtajit ábrázolni kell
     */
    public DoorsView(Room currRoom) {
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
        if(!doors.isEmpty()) doors.clear();
        for (int i = 0; i < currentRoom.getDoors().size(); i++) {
            doors.add("Door #" + String.valueOf(currentRoom.getDoors().get(i).getID()));
        }
        doorList = new JComboBox<>(doors.toArray(new String[0]));
    }
}
