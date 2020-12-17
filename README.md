# Cikalleshi_SNMP
Systeme und Netze: SNMP-Tool programmieren

### Prerequisites
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

### Installing

In die Releases kann man die `SNMPS.jar`-Datei heruntergeladen werden.

Die jar-Datei sollte mit dem folgenden Befehl ausgeführt werden:

```
java --module-path {PATH_TO_FX_NUMBER} --add-modules javafx.controls,javafx.fxml,javafx.base,javafx.graphics,javafx.web,javafx.swing -jar {PATH_TO_JAR}
```


## Running the Program

Im Programm muss man das gewünschte Subnet und community angeben.


### Break down into end to end tests

Explain what these tests test and why

```
Give an example
```

### And coding style tests

Explain what these tests test and why

```
Give an example
```

## Deployment

Add additional notes about how to deploy this on a live system

## Built With

* [Dropwizard](http://www.dropwizard.io/1.0.2/docs/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [ROME](https://rometools.github.io/rome/) - Used to generate RSS Feeds

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## Authors

* **Billie Thompson** - *Initial work* - [PurpleBooth](https://github.com/PurpleBooth)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Hat tip to anyone whose code was used
* Inspiration
* etc
