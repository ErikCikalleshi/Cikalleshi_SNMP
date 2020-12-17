package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.soulwing.snmp.VarbindCollection;
import java.io.IOException;
import java.net.InetAddress;


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


    public void scanNetwork() {
        int timeout = 1000;
        for (int i = 0; i < 255; i++) {
            final int j = i;
            new Thread(() -> {
                try {
                    String host = ipField.getText() + j;
                    InetAddress address = InetAddress.getByName(host);
                    if (address.isReachable(timeout)) {
                        table01.getItems().add(new Client("192.168.1." + j, CommField.getText()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

}
