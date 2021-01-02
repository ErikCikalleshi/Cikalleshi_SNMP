package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import org.soulwing.snmp.*;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Controller {
    private static Controller instance;
    @FXML
    public TextField ipField = new TextField();
    @FXML
    public TextField community = new TextField();
    @FXML
    public ChoiceBox<String> getMethod = new ChoiceBox<>();
    @FXML
    public TextField command = new TextField();
    @FXML
    public MenuItem loadMib = new MenuItem();
    @FXML
    private TableView<Client> table01 = new TableView<>();
    @FXML
    public TableColumn<Client, String> Test;
    @FXML
    private Button scanNetworkBtn;
    @FXML
    public TableView<Varbinds> table02 = new TableView<>();
    @FXML
    public TableColumn<Varbinds, String> Name;
    @FXML
    public TableColumn<Varbinds, String> OID;
    @FXML
    private TableColumn<Varbinds, String> Value;
    @FXML
    public TableColumn<Varbinds, String> IP;
    FileChooser fileChooser = new FileChooser();

    private ArrayList<Client> clients = new ArrayList<>();

    public static Controller getInstance() {
        return instance;
    }

    public String getCommandOID(){ return command.getText(); }

    public void setCommand(String command){
        this.command.setText(command);
    }

    public void load(String ip, String community) throws InterruptedException, ExecutionException {
        VarbindCollection v = null;
        v = Main.read(ip, community, getMethod.getValue());
        if (v == null) {
            for (Client client : clients) {
                if (client.getTest().getText().equals(ip)) {
                    client.getTest().setStyle("-fx-background-color: orangered");
                }
            }
        }
    }

    @FXML
    public void initialize() {
        instance = this;
        ipField.setText("10.10.30.0");
        ipField.setPromptText("ex.: 192.168.1.0");
        community.setText("public");
        community.setPromptText("Community-Name");
        command.setText(".1.3");
        command.setPromptText("ex.: .1.3.6.1.2.1.1.5.0 or sysName");

        getMethod.getItems().addAll("get", "getNext");
        getMethod.getSelectionModel().select(0);

        Name.setCellValueFactory(new PropertyValueFactory<>("Name"));
        OID.setCellValueFactory(new PropertyValueFactory<>("OID"));
        Value.setCellValueFactory(new PropertyValueFactory<>("Value"));
        Test.setCellValueFactory(new PropertyValueFactory<>("Test"));
        Test.setCellValueFactory(new PropertyValueFactory<>("Dump"));
        IP.setCellValueFactory(new PropertyValueFactory<>("IP"));
        table02.getColumns().clear();
        table02.getColumns().add(Name);
        table02.getColumns().add(OID);
        table02.getColumns().add(Value);
        table02.getColumns().add(IP);
    }

    public void getIpBtn(ActionEvent actionEvent) {
        //read();
    }

    public void scanNetwork(ActionEvent actionEvent) throws InterruptedException, ExecutionException {
        int timeout = 2000;
        String host = ipField.getText();
        String[] temp = host.split("\\.");
        ExecutorService executor = Executors.newCachedThreadPool();
        table01.getItems().clear();
        clients.clear();
        for (int i = 1; i < 255; i++) {
            Thread.sleep(2);
            final int j = i;
            executor.execute(() -> {
                try {
                    temp[3] = String.valueOf(j);
                    String x = String.join(".", temp);
                    InetAddress address = InetAddress.getByName(x);

                    if (address.isReachable(timeout)) {
                        synchronized (this){
                            clients.add(new Client(x, community.getText()));
                        }
                        load(x,community.getText());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        }
        Thread.sleep(200);

        executor.shutdown();
        for (Client client : clients) {
            table01.getItems().add(client);
            System.out.println(client.getIp());
        }
        try {
            if (executor.awaitTermination(200, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void click(MouseEvent mouseEvent) {
        System.out.println("ssd");
    }

    public void getInformation(String ip, String community) throws ExecutionException, InterruptedException {
        VarbindCollection v = null;
        v = Main.read(ip, community, getMethod.getValue());
        for (int i = 0; i < v.size(); i++) {
            table02.getItems().add(new Varbinds(v.get(i), ip));
        }
    }

    public void loadOwnMib(ActionEvent actionEvent) throws IOException {
        Main.file = fileChooser.showOpenDialog(Main.getpStage());
        Main.mib.load(Main.file);
    }

    public void commandOnAction(ActionEvent actionEvent) {

    }
}
