# WinThing

[![Build Status](https://travis-ci.org/mghaan/winthing.svg?branch=master)](https://travis-ci.org/mghaan/winthing)

A modular background service that makes Windows remotely controllable through MQTT. For home automation and Internet of Things.

## Requirements

Java SE 8 or Java SE 11 is required on your local machine. Other Java versions untested.<br>

## Compilation

Maven is required to build Java application. For convenience the Maven build file contains execution to produce a Windows executable.  

    mvn clean package
    mvn package

## Running

Download the Windows executable, create `winthing.conf` in the same directory, then run .exe file: 

	winthing-1.3.0.exe
	
or you can execute the Java file:

    java -jar winthing-1.3.0.jar
    
*1.3.0 represents the application version and is changing with each update. Feel free to rename the file to winthing.exe to keep the same name.*

### Tray icon, console

After launching a tray icon will appear on the taskbar. You can click on the icon to open console log or you can quit WinThing.

## Configuration

Configuration parameters can be passed from command line or they can be placed in configuration files in the working directory from where you launch WinThing.

| Property   | Description | Default |
|------------|-------------|---------|
| `broker`   | `host` or `host:port` (if port is not 1883) to connect to MQTT broker | 127.0.0.1:1883 |
| `username` | Username used when connecting to MQTT broker | mqtt |
| `password` | Password used when connecting to MQTT broker | mqtt |
| `clientid` | Client ID to present to the broker | WinThing |
| `reconnect`| Time interval between connection attempts in seconds | 5 |
| `prefix`   | Topic prefix | winthing |

### Command line parameters

Example how to pass parameters from command line:

	java -Dbroker="127.0.0.1:1883" -jar winthing-1.2.0.jar

### winthing.conf

WinThing will look for this file in the current working directory (directory from where you launched WinThing). Create this file and put desired parameters into it.

Example file:

	broker = "127.0.0.1:1883"
	username = "mqtt"
	password = "somesecret"
	
### winthing.ini

By default WinThing executes any command it receives in the system/commands/run topic. Create this file in the current working directory to whitelist only specific commands. The file contains an unique string identifier (used as payload in the MQTT message, see below) and path to executable.

Example file:

	notepad = "c:/windows/system32/notepad.exe"
	adobe = "c:\\program files\\adobe\\reader.exe"
	
*Note you can use slash* ' / ' *or double backslash* ' \\\\ ' *as path separator.*
	
## Logging

You can open application log by clicking on the tray icon. To log into `winthing.log` file in the current working directory run WinThing with the `-debug` parameter.

	winthing.exe -debug

## Supported messages

The payload of all messages is either empty or a valid JSON element (possibly a primitive, like a single integer). This means, specifically, that if an argument is supposed to be a single string, it should be sent in double quotes.

Example valid message payloads:

* `123`
* `true`
* `"notepad.exe"`
* `[1024, 768]`
* `["notepad.exe", "C:\\file.txt", "C:\\"]` (note that JSON string requires escaped backslash)

### Topics broadcasted by WinThing

| Topic | Value | Properties |
|-------|-------|------------|
| `winthing/system/online` | `true`/`false` | QoS 2, Persistent
 
`true` when WinThing is running, `false` otherwise. 

WinThing registers a "last will" message with the broker ensure it is set to `false` when WinThing disconnects.

### Topics to trigger WinThing actions

#### System

| Topic | Payload | Action |
|-------|---------|--------|
| `winthing/system/commands/shutdown`  | - | Shut down computer |
| `winthing/system/commands/reboot`    | - | Reboot computer |
| `winthing/system/commands/suspend`   | - | Put computer to sleep |
| `winthing/system/commands/hibernate` | - | Hibernate computer |
| `winthing/system/commands/open`      | uri:string | Open an URI, like a website in a browser or a disk location in a file browser. |
| `winthing/system/commands/run`       | [command:string, arguments:string, workingDirectory:string] | Run a command. Arguments and working directory are optional (empty string and null by default).<br> |

If whitelist is enabled, only the command as unique identifier is required. The identifier is checked against the whitelist file (see **whitelist.ini** above).

#### Desktop

| Topic | Payload | Action |
|-------|---------|--------|
| `winthing/desktop/commands/close_active_window` | - | Closes currently active window. |
| `winthing/desktop/commands/set_display_sleep` | displaySleep:boolean | Puts the display to sleep (on true) or wakes it up (on false). |

#### Keyboard

| Topic | Payload | Action |
|-------|---------|--------|
| `winthing/keyboard/commands/press_keys` | [key:string...] | Simulates pressing of given set of keyboard keys. Keys are specified by name. List of available key names and aliases can be found [here](src/main/java/com/fatico/winthing/windows/input/KeyboardKey.java). |

#### ATI Radeon display driver

| Topic | Payload | Action |
|-------|---------|--------|
| `winthing/radeon/commands/set_best_resolution` | - | Sets the screen to the best available resolution. |
| `winthing/radeon/commands/set_resolution` | [widthInPixels:integer, heightInPixels:integer] | Sets the screen to the given resolution. |

## License

Copyright 2015-2016 Mikołaj Siedlarek &lt;mikolaj@siedlarek.pl&gt;

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this software except in compliance with the License.
You may obtain a copy of the License at

> http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
