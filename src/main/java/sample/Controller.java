package sample;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import org.apache.commons.lang3.ArrayUtils;
import org.soulwing.snmp.*;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


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
    private boolean frstScan;

    public static Controller getInstance() {
        return instance;
    }

    public void load(String ip, String community) throws InterruptedException {
        VarbindCollection v = null;
        try {
            v = Main.read(ip, community);
        } catch (IOException | ExecutionException | InterruptedException e) {
            System.out.print("");
        }
        if (v == null) {
            System.out.println(ip + "not reachable with SNMP");
            table01.getItems().stream().sort6ed();
        }else{
            table01.getItems().add(new Client(ip, "public"));
        }
        /*if (!frstScan){
            table02.getItems().clear();
            for (int i = 0; i < v.size(); i++) {
                table02.getItems().add(new Varbinds(v.get(i)));
            }
        }*/


        /*if (frstScan) {
            synchronized (this){
                table01.getItems().add(new Client(ip, "public"));
            }

        } else {
            table02.getItems().clear();
            for (int i = 0; i < v.size(); i++) {
                table02.getItems().add(new Varbinds(v.get(i)));
            }
        }*/


    }

    @FXML
    public void initialize() {
        instance = this;
        ipField = new TextField();
        CommField = new TextField();
        ipField.setText("10.10.30.0");
        CommField.setText("public");
        frstScan = true;
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

    public void scanNetwork(ActionEvent actionEvent) throws InterruptedException {
        int timeout = 500;
        String host = ipField.getText();
        String[] temp = host.split("\\.");
        ExecutorService executor = Executors.newCachedThreadPool();
        for (int i = 1; i < 255; i++) {
            final int j = i;
            executor.execute(() -> {
                try {
                    System.out.println(j);
                    temp[3] = String.valueOf(j);
                    String x = String.join(".", temp);
                    InetAddress address = InetAddress.getByName(x);
                    if (address.isReachable(timeout)) {
                        System.out.println(address.toString());
                        //System.out.println("in" + CommField.getText());
                        table01.getItems().add(new Client(x, "public"));
                        load(x, "public");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        executor.shutdown();
        if(!executor.awaitTermination(1000, TimeUnit.MILLISECONDS)){
            frstScan = false;
        }

    }


    public void click(MouseEvent mouseEvent) {
        System.out.println("ssd");
    }
}
