package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import org.apache.commons.net.util.SubnetUtils;
import org.soulwing.snmp.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;


public class Controller {
    private static Controller instance;
    @FXML public TextField ipField = new TextField();
    @FXML public TextField community = new TextField();
    @FXML public ChoiceBox<String> getMethod = new ChoiceBox<>();
    @FXML public TextField command = new TextField();
    @FXML public MenuItem loadMib = new MenuItem();
    @FXML public ProgressBar loadingBar = new ProgressBar();
    @FXML public Button notification;
    @FXML public TableView<TrapTable> trapTable = new TableView<>();
    @FXML public TableColumn<TrapTable, String> OidName;
    @FXML public TableColumn<TrapTable, String>  ValueTrap;
    @FXML public TextField port = new TextField();
    @FXML private TableView<Client> table01 = new TableView<>();
    @FXML public TableColumn<Client, String> Test;
    @FXML private Button scanNetworkBtn;
    @FXML public TableView<Varbinds> table02 = new TableView<>();
    @FXML public TableColumn<Varbinds, String> Name;
    @FXML public TableColumn<Varbinds, String> OID;
    @FXML private TableColumn<Varbinds, String> Value;
    @FXML public TableColumn<Varbinds, String> IP;
    @FXML public TableColumn<TrapTable, String> Type;
    @FXML public TableColumn<TrapTable, String> SourceIP;

    FileChooser fileChooser = new FileChooser();
    private ArrayList<Client> clients = new ArrayList<>();
    public static Controller getInstance() {
        return instance;
    }



    public Runnable load(String ip, String community) throws InterruptedException, ExecutionException {
        VarbindCollection v = null;
        v = SNMPScanner.read(ip, community, getMethod.getValue());
        if (v == null) {
            for (Client client : clients) {
                if (client.getTest().getText().equals(ip)) {
                    client.getTest().setStyle("-fx-background-color: orangered");
                    //client.getTest().setDisable(true);
                }
            }
        }
        return null;
    }

    @FXML
    public void initialize() {
        instance = this;
        ipField.setText("10.10.30.1/24");
        ipField.setPromptText("ex.: 192.168.1.0");
        community.setText("public");
        community.setPromptText("Community-Name");
        command.setText(".1.3");
        command.setPromptText("ex.: .1.3.6.1.2.1.1.5.0 or sysName");
        port.setText("10301");
        port.setPromptText("Port");

        getMethod.getItems().addAll("Basic", "get", "getNext");
        getMethod.getSelectionModel().select(0);

        Name.setCellValueFactory(new PropertyValueFactory<>("Name"));
        OID.setCellValueFactory(new PropertyValueFactory<>("OID"));
        Value.setCellValueFactory(new PropertyValueFactory<>("Value"));
        Test.setCellValueFactory(new PropertyValueFactory<>("Test"));
        Test.setCellValueFactory(new PropertyValueFactory<>("HBox"));
        IP.setCellValueFactory(new PropertyValueFactory<>("IP"));

        SourceIP.setCellValueFactory(new PropertyValueFactory<>("SourceIP"));
        Type.setCellValueFactory(new PropertyValueFactory<>("Type"));
        OidName.setCellValueFactory(new PropertyValueFactory<>("OidName"));
        ValueTrap.setCellValueFactory(new PropertyValueFactory<>("ValueTrap"));

        trapTable.getColumns().clear();
        trapTable.getColumns().add(OidName);
        trapTable.getColumns().add(ValueTrap);
        trapTable.getColumns().add(Type);
        trapTable.getColumns().add(SourceIP);

        table02.getColumns().clear();
        table02.getColumns().add(Name);
        table02.getColumns().add(OID);
        table02.getColumns().add(Value);
        table02.getColumns().add(IP);
    }

    public void getIpBtn(ActionEvent actionEvent) {
        //read();
    }

    public void scanNetwork(ActionEvent actionEvent) throws InterruptedException, ExecutionException, MalformedURLException {
        table01.getItems().clear();
        clients.clear();
        String host = ipField.getText();
        Thread.sleep(500);
        if (!host.contains("/")) {
            String[] temp = host.split("\\.");
            if (temp[temp.length - 1].equals("0")) {
                scanDefaultSubnet(temp);
            } else {
                scanOneIP(host);
            }
        } else {
            SubnetUtils utils = new SubnetUtils(host);
            String[] addresses = utils.getInfo().getAllAddresses();
            System.out.println(addresses.length);
            CountDownLatch latch = new CountDownLatch(10);

            ExecutorService executor = Executors.newCachedThreadPool();
            Instant starts = Instant.now();
            new Thread(() -> {
                double counter = 0;
                AtomicLong waitingTime = new AtomicLong(2L);
                for (String ip : addresses) {
                    executor.submit(() -> {
                        try {
                            InetAddress address = InetAddress.getByName(ip);

                            if (address.isReachable(1000)) {
                                System.out.println(address.toString());
                                synchronized (this) {
                                    waitingTime.set(2L);
                                    clients.add(new Client(ip, community.getText()));
                                    //System.out.println(clients.get(clients.size() - 1).getIp());
                                }
                                load(ip, community.getText());
                            }else {
                                synchronized (this){
                                    waitingTime.set(1);
                                }

                            }
                            //latch.countDown();
                        } catch (InterruptedException | IOException | ExecutionException e) {
                            Thread.currentThread().interrupt();
                        }
                    });
                    try {
                        synchronized (this){
                            counter++;
                            loadingBar.setProgress(counter/addresses.length);
                        }
                        Thread.sleep(waitingTime.get());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Instant ends = Instant.now();
                System.out.println(Duration.between(starts, ends));
                try {
                    if (!executor.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executor.shutdownNow();
                }
                System.out.println(clients.size());
                for (Client client : clients) {
                    table01.getItems().add(client);
                }
            }).start();



        }
    }


    private void scanOneIP(String ip) throws ExecutionException, InterruptedException, MalformedURLException {
        clients.add(new Client(ip, community.getText()));
        load(ip, community.getText());
        table01.getItems().add(clients.get(0));
    }

    private void scanDefaultSubnet(String[] temp) throws InterruptedException {
        int timeout = 2000;
        ExecutorService executor = Executors.newCachedThreadPool();

        for (int i = 1; i < 256; i++) {
            Thread.sleep(2);
            final int j = i;
            executor.execute(() -> {
                try {
                    temp[3] = String.valueOf(j);
                    String x = String.join(".", temp);
                    InetAddress address = InetAddress.getByName(x);

                    if (address.isReachable(timeout)) {
                        synchronized (this) {
                            clients.add(new Client(x, community.getText()));
                            System.out.println(clients.get(clients.size() - 1).getIp());
                        }
                        load(x, community.getText());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        }
        Thread.sleep(400);

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
        v = SNMPScanner.read(ip, community, getMethod.getValue());
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

    public void notifications(ActionEvent actionEvent) {
        int portNumber = Integer.parseInt(port.getText());
        new Thread(() ->{
            Listener l;
            if(portNumber >= 1024 || portNumber < 65536){
                l = new Listener(portNumber);
            }else{
                port.setText("");
                throw new IllegalArgumentException("Port Invalid");
            }
            l.startListener();
            try {
                Thread.sleep(10000);
            }catch (Exception e){
                System.out.println("Listener stopped");
            }finally {
                l.stopListener();
            }
        }).start();

    }

    public String getCommandOID() {
        return command.getText();
    }

    public void setCommand(String command) {
        this.command.setText(command);
    }

    public TableView<TrapTable> getTrapTable(){
        return trapTable;
    }
}
