# Monitor & Information Rest API for Raspberry PI

This is a very lean little project based on SpringBoot 2.0M7 and [Pi4J](http://pi4j.com). I'm planning to use this in a SmartThings SmartApp later so I can see my Pi's status in the SmartThings app. The idea came from [here](https://github.com/nicholaswilde/berryio-smartthings)

# Pre-requisites

* [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Maven 3x](https://maven.apache.org/download.cgi)

# Build

Simply

```bash
mvn clean install
```

# Install on Pi to Start at Boot Up

Change the `src/main/resource/application.properties` file and set the IP address or hostname of the Pi for the variable `upnp.config.address`. You can also pass this information with the `-Dupnp.config.address=<myipaddress>` parameter at startup

```bash
sudo nano /etc/rc.local
```

Then add the below line above the last one (that exits)

```bash
java -jar /path/to/jar/raspberrypi.monitor.jar &
```

# Usage

```
http://raspberrypi:8080/api/pi
```

Would yield something like this:

```json
{
"cpuTemperature": 52.1,
"serialNumber": "0000000000000000",
"cpuCoreVoltage": 1.2,
"modelName": "ARMv7 Processor rev 4 (v7l)",
"boardType": "RaspberryPi_3B",
"totalMemory": 967700480,
"usedMemory": 221253632,
"freeMemory": 401907712,
"sharedMemory": 14475264,
"javaVersion": "1.8.0_152",
"hostname": "raspberrypi",
"ipAddresses": [
"192.168.1.40",
"172.17.0.1"
],
"cpuFrequency": 600000000
}
```
