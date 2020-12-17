package sample;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.soulwing.snmp.VarbindCollection;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;


public class Controller {
    private static Controller instance;
    @FXML
    private TableView<Client> table01;
    @FXML
    public TableColumn<Client, String> Test;
    @FXML
    private TextField ipField;
    @FXML
    private TextField CommField;
    @FXML
    private Button scanNetworkBtn;
    @FXML
    public TableView<Varbinds> table02;
    @FXML
    public TableColumn<Varbinds, String> Name;
    @FXML
    public TableColumn<Varbinds, String> OID;
    @FXML
    private TableColumn<Varbinds, String> Value;

    public static Controller getInstance() {
        return instance;
    }

    public void load(String ip, String community) throws IOException {
        VarbindCollection v = Main.read(ip, community);
        for (int i = 0; i < v.size(); i++) {
            table02.getItems().add(new Varbinds(v.get(i)));
        }

    }

    @FXML
    public void initialize() {
        instance = this;
        Name.setCellValueFactory(new PropertyValueFactory<>("Name"));
        OID.setCellValueFactory(new PropertyValueFactory<>("OID"));
        Value.setCellValueFactory(new PropertyValueFactory<>("Value"));
        Test.setCellValueFactory(new PropertyValueFactory<>("Test"));
        table02.getColumns().clear();
        table02.getColumns().add(Name);
        table02.getColumns().add(OID);
        table02.getColumns().add(Value);
    }

    public void getIpBtn(ActionEvent actionEvent) {
        //read();
    }

    public void scanNetwork(ActionEvent actionEvent) {
        int timeout = 1000;
        for (int i = 0; i < 255; i++) {
            final int j = i;
            new Thread(() -> {
                try {
                    String host = ipField.getText();
                    String[] temp = host.split("\\.");
                    temp[3] = String.valueOf(j);
                    String x = String.join(".", temp);
                    InetAddress address = InetAddress.getByName(x);
                    if (address.isReachable(timeout)) {
                        //System.out.println(x);
                        //System.out.println("in" + CommField.getText());
                        table01.getItems().add(new Client(x, "public"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public void click(MouseEvent mouseEvent) {
        System.out.println("ssd");
    }
}
