package ViewModels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;

import Interfaces.Observer;
import Views.MenuView;
import Views.GameView;

public class MenuViewModel extends MenuView implements Observer {
    public ActionListener newGameActionListener;
    public ActionListener loadGameActionListener;
    public ActionListener startActionListener;
    public ActionListener addPlayListener;
    public ActionListener removePlayListener;

    public MenuViewModel(GameView g){
        super(g);

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(studentArray);
        removePlayerBox.setModel(model);

        newGameActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gv.remove(menuPanel);
                gv.add(playerManagerPanel);
                gv.revalidate();
            }           
        };

        loadGameActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO load game
            } 
        };



        startActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gv.remove(playerManagerPanel);
                gv.showGame();
            } 
        };

        addPlayListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addStudent();

                String[] temp = new String[studentList.size()];

                for (int i = 0; i < studentList.size(); i++){
                    temp[i] = studentList.get(i);
                }

                studentArray = temp;

                model.removeAllElements();
                for (String student : studentList) {
                    model.addElement(student);
                }
            } 
        };

        removePlayListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                studentList.remove(removePlayerBox.getSelectedIndex());

                String[] temp = new String[studentList.size()];

                for (int i = 0; i < studentList.size(); i++){
                    temp[i] = studentList.get(i);
                }

                studentArray = temp;
                model.removeAllElements();
                for (String student : studentList) {
                    model.addElement(student);
                }
            } 
        };

        newGameButton.addActionListener(newGameActionListener);
        loadGameButton.addActionListener(loadGameActionListener);
        startButton.addActionListener(startActionListener);
        addPlayButton.addActionListener(addPlayListener);
        removePlayerButton.addActionListener(removePlayListener);
    }

    @Override
    public void update() {
        
    }
}
