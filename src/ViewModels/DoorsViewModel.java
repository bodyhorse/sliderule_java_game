package ViewModels;

import GameLogic.GameController;
import Views.DoorsView;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DoorsViewModel{

    /**
     * a chooseDoor gomb ActionListenere
     */
    public ActionListener chooseDoorActionListener;

    /**
     * A controllerhez tartozó view referenciája
     */
    private DoorsView doorsView;

    /**
     * Konstruktor
     */
    public DoorsViewModel(DoorsView dV){
        doorsView = dV;
        chooseDoorActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String output = "";

                if(String.valueOf(doorsView.getDoorList().getSelectedItem()) == "Stay"){
                    output = "move";
                }
                else{
                    String [] idCut = String.valueOf(doorsView.getDoorList().getSelectedItem()).split("#");
                    if(idCut[0].startsWith("T")){
                        output = "teleport " + idCut[1];
                    }
                    else{
                        output = "move " + idCut[1];
                    }
                }

                //GameController.getInstance().setLastInput(output);
                GameController.getInstance().terminal(output);
                doorsView.refreshDoors();
            }
        };
        doorsView.getChooseButton().addActionListener(chooseDoorActionListener);
    }
}
