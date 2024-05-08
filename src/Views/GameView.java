package Views;

import Entity.Student;
import GameLogic.GameController;
import GameMap.Door;
import GameMap.Room;
import Interfaces.*;
import Item.SlideRule;
import ViewModels.MenuViewModel;

import javax.swing.*;
import java.awt.*;
//import ViewModels.InventoryViewModel;

public class GameView extends JFrame implements Observer {
    //private InventoryViewModel.InventoryTabbedPane inventoryTabbedPane;

    private MenuView menu = new MenuViewModel(this);

    public GameView() {
        setSize(712, 712);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Room rvm = new Room(false, 10, 1 );
        Room rvm2 = new Room(false, 20, 2);
        Room rvm3 = new Room(false, 10, 3);
        Room rvm4 = new Room(false, 10, 4);


        for(int i = 0; i < 20; ++i) {
            if(i < 7) rvm.addEntity(new Student("János", i, rvm));

            rvm2.addEntity(new Student("Béla", i, rvm2));
        }

        for(int i = 0; i < 500; ++i) {
            if(i < 2) rvm.addItem(new SlideRule(i));
            rvm2.addItem(new SlideRule(i));
        }

        GameController.getInstance().currentEntity = new Student("Jóska", 1, rvm);

        rvm.addDoor(new Door(rvm, rvm2, false, Door.Direction.BOTH, 10));
        rvm.addDoor(new Door(rvm2, rvm3, false, Door.Direction.BOTH, 11));
        rvm.addDoor(new Door(rvm2, rvm4, false, Door.Direction.BOTH, 12));
        //rvm2.setNumOfDoors(25);



        //this.add(menu);
        this.add(menu.menuPanel);
        //showGame();


        this.setVisible(true);
        setLocationRelativeTo(null);
    }


    public void showGame(){
        //this.removeAll();
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new RoomView(GameController.getInstance().currentEntity.getCurrentRoom()), gbc);
        //this.add(new RoomView(GameController.getInstance().currentEntity.getCurrentRoom()));
        //this.add(inventoryTabbedPane);

        JPanel panel2 = new JPanel();
        panel2.setBackground(Color.BLUE);
        panel2.setPreferredSize(new Dimension(200, 512));
        gbc.gridx = 1;
        gbc.gridy = 0;
        mainPanel.add(panel2, gbc);

        // Panel 3: 512x200 in the bottom left corner
        gbc.gridx = 0;
        gbc.gridy = 1;
        DialougeView dv = new DialougeView();
        dv.setPreferredSize(new Dimension(512,200));
        mainPanel.add(dv,gbc);
        

        // Doors Panel: 200x200 in the bottom right corner
        JPanel doorsPanel = new JPanel();
        //doorsPanel.setBackground(Color.YELLOW);
        //doorsPanel.setPreferredSize(new Dimension(200, 200));
        gbc.gridx = 1;
        gbc.gridy = 1;
        doorsPanel.add(new DoorsView(GameController.getInstance().currentEntity.getCurrentRoom()), gbc);
        mainPanel.add(doorsPanel, gbc);

        this.add(mainPanel);
        this.setSize(712, 712);
        this.pack();
        this.setVisible(true);
        this.revalidate();
        this.repaint();
    }

    @Override
    public void update() {

    }
}
