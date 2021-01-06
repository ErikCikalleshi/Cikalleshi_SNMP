# Cikalleshi_SNMP
Systeme und Netze: SNMP-Tool programmieren

## Prerequisites
* Java 14 oder höher muss installiert sein. Zusätzlich muss man auch [JavaFX](https://gluonhq.com/download/javafx-15-0-1-sdk-windows/) 14 oder 15 herunterladen.   
* Das Programm braucht die Library von [tnm4j](https://github.com/soulwing/tnm4j).   
* Es ist jedoch empfehlenswert Maven zu verwenden.

```
    <dependencies>
        <!-- https://mvnrepository.com/artifact/org.soulwing.snmp/tnm4j -->
        <dependency>
            <groupId>org.soulwing.snmp</groupId>
            <artifactId>tnm4j</artifactId>
            <version>1.0.11</version>
        </dependency>
    </dependencies>
```

## Installing

In die Releases kann man die [SNMP.jar](https://github.com/Th3RapidK1ller/Cikalleshi_SNMP/releases/download/ThirdMileStone/SNMP.jar)-Datei herunterladen.

## Running the Program

Die jar-Datei sollte mit dem folgenden Befehl ausgeführt werden:

```
java --module-path {PATH_TO_FX_NUMBER} --add-modules javafx.controls,javafx.fxml,javafx.base,javafx.graphics,javafx.web,javafx.swing -jar {PATH_TO_JAR}
```
**Reminder: JavaFx 15 und JDK 14 muss installiert sein**

Um nachzuschauen, welche Version man auf dem Rechner installiert hat, führen Sie folgenden Befehl aus:
```
java -version
```
## How to use

Man muss nur das gewünschte Subnet und die Community eingeben.
Dann wählen Sie die IP-Adresse aus.
![Scanning for Network](src/example.png)


## Status

Im Moment kann ich mittels UI nur 6 verschiedene Information auslesen und ein Netzwerk scannen.
Der User kann selbst das Subnet und Community auswählen.

## Future

Das Programm muss noch umstrukturiert werden: 
* ~~Dropdown-Box für die Methode Get/GetNext und Community~~
* ~~Standarmäßig public oder private Community-String~~
* ~~~Load -und Unload von eigene Mib-Dateien~~~
* ~~~Eigene MIBs bzw. OID eingeben (sysName bzw. .1.3.6.1.2.1.1.5.0)~~~
* CSS
* ~~~IP Range scan~~~
* **Traps oder Informs empfangen muss noch programmiert werden**


