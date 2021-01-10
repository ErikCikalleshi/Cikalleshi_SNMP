package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.apache.commons.net.util.SubnetUtils;
import org.soulwing.snmp.*;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;


public class Controller {
    private static Controller instance;
    @FXML public TextField ipField = new TextField();
    @FXML public TextField community = new TextField();
    @FXML public TextField command = new TextField();
    @FXML public TextField port = new TextField();

    @FXML public ChoiceBox<String> getMethod = new ChoiceBox<>();

    @FXML public MenuItem loadMib = new MenuItem();
    @FXML public ProgressBar loadingBar = new ProgressBar();
    @FXML public Button notification;

    @FXML public TableView<TrapTable> trapTable = new TableView<>();
    @FXML public TableView<Varbinds> table02 = new TableView<>();
    @FXML private TableView<Client> table01 = new TableView<>();
    @FXML public TableColumn<TrapTable, String> OidName;
    @FXML public TableColumn<TrapTable, String>  ValueTrap;
    @FXML public TableColumn<Varbinds, String> Name;
    @FXML public TableColumn<Varbinds, String> OID;
    @FXML private TableColumn<Varbinds, String> Value;
    @FXML public TableColumn<Varbinds, String> IP;
    @FXML public TableColumn<TrapTable, String> Type;
    @FXML public TableColumn<TrapTable, String> SourceIP;

    @FXML public ProgressIndicator timer = new ProgressIndicator();

    @FXML public TableColumn<Client, String> Test;
    @FXML private final Button scanNetworkBtn = new Button();


    FileChooser fileChooser = new FileChooser();
    private ArrayList<Client> clients = new ArrayList<>();

    public static Controller getInstance() {
        return instance;
    }

    /**
     * This function loads calls the Function SNMPScanner.read() and returns the VarbindCollection
     * If the return is null that means that the ip is reachable but not accessable with SNMP
     * @param ip ip that needs to be checked
     * @param community Community
     * @throws InterruptedException Function SNMPScanner.read() throws InterruptedException
     * @throws ExecutionException Function SNMPScanner.read() throws InterruptedException
     */
    public void load(String ip, String community) throws InterruptedException, ExecutionException {
        VarbindCollection v;
        v = SNMPScanner.read(ip, community, getMethod.getValue());
        if (v == null) {
            for (Client client : clients) {
                if (client.getTest().getText().equals(ip)) {
                    client.getTest().setDisable(true);
                    client.getTest().setStyle("-fx-background-color: orangered; -fx-text-fill: white; -fx-opacity: 1");
                }
            }
        }
    }

    /**
     * The Methode initializes the tables and all default Textfields
     */
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

    /**
     * Function decides based on the input from the IPField which function to call
     * @param actionEvent Button "Scan Network" onAction
     * @throws Exception Thread and the other Functions throws more than one Exception
     */
    public void scanNetwork(ActionEvent actionEvent) throws Exception{
        table01.getItems().clear();
        clients.clear();
        String host = ipField.getText();
        if (!host.contains("/")) {
            String[] temp = host.split("\\.");
            if (temp[temp.length - 1].equals("0")) {
                scanDefaultSubnet(temp); //scan from .1 to .255 (192.168.1.0)
            } else {
                scanOneIP(host); //Single IP
            }
        } else {
            scanSubnet(); //scan with Subnetmask
        }
    }

    /**
     * This function scans the network based on the Subnetmask
     */
    private void scanSubnet() {
        SubnetUtils utils = new SubnetUtils(ipField.getText()); //SubnetUtils is a external library
        String[] addresses = utils.getInfo().getAllAddresses();
        ExecutorService executor = Executors.newCachedThreadPool();  //For every IP-Address we start a new Thread (max is Integer.MAX_VALUE)
        new Thread(() -> {
            double counter = 0;
            AtomicLong waitingTime = new AtomicLong(2L);
            for (String ip : addresses) {
                executor.submit(() -> {
                    try {
                        InetAddress address = InetAddress.getByName(ip);
                        if (address.isReachable(1500)) { //if the address isReachable than add to the ArrayList a new found Client
                            synchronized (this) {
                                waitingTime.set(2L);
                                clients.add(new Client(ip, community.getText()));
                            }
                            load(ip, community.getText()); //check if address is with SNMP reachable
                        }else {
                            synchronized (this){
                                waitingTime.set(1L);
                            }
                        }

                    } catch (InterruptedException | IOException | ExecutionException e) {
                        Thread.currentThread().interrupt();
                    }
                });
                try {
                    synchronized (this){
                        counter++;
                        loadingBar.setProgress(counter/addresses.length);
                    }
                    Thread.sleep(waitingTime.get()); //after every scan wait 1L ms or 2L ms (optimized for bigger scans)
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                //awaitTermination waits 2,5 Seconds for existing tasks to terminate
                if (!executor.awaitTermination(2500, TimeUnit.MILLISECONDS)) {
                    executor.shutdownNow(); //disable all running tasks
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
            for (Client client : clients) {
                table01.getItems().add(client);
            }
        }).start();
    }


    private void scanOneIP(String ip) throws ExecutionException, InterruptedException {
        clients.add(new Client(ip, community.getText()));
        load(ip, community.getText());
        table01.getItems().add(clients.get(0));
        scanNetworkBtn.setDisable(false);
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
                        }
                        load(x, community.getText());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        try {
            if (executor.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        for (Client client : clients) {
            table01.getItems().add(client);
        }
    }

    /**
     * Function adds the returned result from SNMPScanner.read() to the table and works after the scan of the network
     * @param ip ip that needs to be checked
     * @param community Community
     * @throws InterruptedException Function SNMPScanner.read() throws InterruptedException
     * @throws ExecutionException Function SNMPScanner.read() throws InterruptedException
     */
    public void getInformation(String ip, String community) throws ExecutionException, InterruptedException {
        VarbindCollection v;
        v = SNMPScanner.read(ip, community, getMethod.getValue());
        for (int i = 0; i < v.size(); i++) {
            table02.getItems().add(new Varbinds(v.get(i), ip));
        }
    }

    /**
     * Function loads own Mib-File
     * @param actionEvent open Dialog
     * @throws IOException load throws IOException
     */
    public void loadOwnMib(ActionEvent actionEvent) throws IOException {
        Main.file = fileChooser.showOpenDialog(Main.getpStage());
        Main.mib.load(Main.file);
    }

    /**
     * The Function starts a Listener that listens for x 10000ms
     * @param actionEvent Button onAction
     */
    public void notifications(ActionEvent actionEvent) {
        int portNumber = Integer.parseInt(port.getText());
        AtomicBoolean wait = new AtomicBoolean(true);
        Thread timerThread = new Thread(()->{
            for (int i = 10000; i > 0 ; i--) {
                timer.setProgress((double)i/ 10000);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            wait.set(false);
        });
        new Thread(() ->{
            Listener l;
            notification.setDisable(true);
            if(portNumber >= 1024 && portNumber < 65536){
                l = new Listener(portNumber);
            }else{
                port.setText("");
                notification.setDisable(false);
                Thread.currentThread().interrupt();
                throw new IllegalArgumentException("Port Invalid");
            }

            l.startListener();
            try {
                timerThread.start();
            }finally {
                while(wait.get()){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                notification.setDisable(false);
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

    public void quit(ActionEvent actionEvent) {
        System.exit(0);
    }
}