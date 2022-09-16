import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.util.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class LogController implements Initializable {

    @FXML
    ListView<String> loglistview;
    public static ObservableList<String> list;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try{

            loglistview.

            list= FXCollections.observableArrayList(new ArrayList<>());
            list.addListener(new ListChangeListener<String>() {
                @Override
                public void onChanged(Change<? extends String> change) {

                        while (change.next()) {
                            if (change.wasAdded()){

                                for(String s: change.getAddedSubList()){
                                    try{
                                        loglistview.getItems().add(s);
                                    }catch (Exception e){
                                        System.out.println(e);
                                    }
                                }
                                //loglistview.getItems().addAll(change.getAddedSubList());
                                loglistview.refresh();
                            }else if(change.wasRemoved()){
                                loglistview.refresh();
                            }
                        }



                }
            });

        }catch (IllegalStateException e){
            //e.printStackTrace();
            System.out.println("eeeeeex"+e);
        }

    }





}
