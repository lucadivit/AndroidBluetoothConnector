# AndroidBluetoothConnector

This repository contains the main android file of the app.
AndroidBluetoothConnector has been written for the Electronic Technologies course 17/18 (Univ. of L'Aquila). The purpose of this project 
is connect smartphone to Sim800C module with bluetooth, and give AT Commands for program it.

## Testing

I tested this app with HC06 module connected with Arduino.

## Instruction
* Launch the app.
* Search devices by clicking on the buttons.
* A list of devices will appear. Click on the element of interest.
* Write something and send by clicking buttons.
* If the remote device answers, a dialog will appear.

## Note
* Because the methods of Bluetooth Management are numerous, i create a class (BluetoothConnectionManagement.java) that semplify the 
interactions. In this class there are, other than bluetooth management methods, many Intent to support Broadcast Receiver. To allow this i
passed the context of MainActivity to constructor of BluetoothConnectionManagement.
* Because HC06 send one byte at time, the application reads one character at time until he recieves the symbol "#". After this, he merges
the characters and forms the String. Thus, when HC06 recieves data, he rensponses with "<desiredWord>#", and the app prints 
"<desiredWord>". A simple Arduino script is in this repository.
* Broadcast Receiver events that, at moment, the app supports are: DEVICE_FOUND, DEVICE_CONNECTED, DEVICE_DISCONNECTED, DISCOVERY_STARTED, 
DISCOVERY_FINISHED, BLUETOOTH_TURNED_OFF, CONNECTION_ERROR, SOCKET_READY, DATA_RECEIVED.
* I develop this app with APIs level 16.
* The apk generated is unsigned.

## To-Do
* Add others Broadcast Receiver events.
* Test with Sim800C module.

## License
This app is under ISC license.
