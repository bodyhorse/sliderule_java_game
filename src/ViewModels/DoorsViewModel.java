package ViewModels;

import GameLogic.GameController;
import Views.DoorsView;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DoorsViewModel{
    public ActionListener chooseDoorActionListener;

    public DoorsViewModel(DoorsView doorsView){
        chooseDoorActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String [] doorDesCut = String.valueOf(doorsView.getDoorList().getSelectedItem()).split("#");
                GameController.getInstance().lastInput = "move " + doorDesCut[1];
                GameController.getInstance().move();
            }
        };
        doorsView.getChooseButton().addActionListener(chooseDoorActionListener);
    }
}
