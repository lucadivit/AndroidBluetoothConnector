#include <SoftwareSerial.h>
int rxPin = 3;
int txPin = 2;
SoftwareSerial bluetooth(rxPin, txPin);
 
String message; //string that stores the incoming message
 
void setup()
{
  Serial.begin(9600); //set baud rate
  bluetooth.begin(9600); //set baud rate
}
 
void loop()
{
  while(bluetooth.available()){
    message+=char(bluetooth.read());
  }
  if(!bluetooth.available())
  {
    if(message!="")
    {//if data is available
      Serial.println(message); //show the data
      bluetooth.print("OK#");
      message=""; //clear the data
      Serial.println("OK");
    }
  }
  delay(300); //delay
}
