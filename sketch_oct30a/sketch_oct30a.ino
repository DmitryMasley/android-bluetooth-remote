#include <SoftwareSerial.h>

SoftwareSerial mySerial(2, 3); // RX, TX
#define BLUETOOTH_SPEED 9600
#define ROBOT_NAME "Lego Robot"

// motor A
int dir1PinA = 13;
int dir2PinA = 12;
int speedPinA = 10;


unsigned long time;
int speed;
int dir;

void setup() {
  
  Serial.begin(38400);
  
  pinMode(dir1PinA, OUTPUT);
  pinMode(dir2PinA, OUTPUT);
  pinMode(speedPinA, OUTPUT);

  time = millis();
  speed = 255;
  dir = 1;
  
  Serial.println("Starting config");
  mySerial.begin(BLUETOOTH_SPEED);
//  delay(1000);
//
//  // Should respond with OK
//  mySerial.print("AT\r\n");
//  waitForResponse();
//
//  // Should respond with its version
//  mySerial.print("AT+VERSION\r\n");
//  waitForResponse();
//
//  // Set pin to 0000
//  mySerial.print("AT+PSWD=0000\r\n");
//  waitForResponse();
//
//  // Set the name to ROBOT_NAME
//  String rnc = String("AT+NAME=") + String(ROBOT_NAME) + String("\r\n"); 
//  mySerial.print(rnc);
//  waitForResponse();
//
//  // Set baudrate to 57600
//  mySerial.print("AT+UART=57600,0,0\r\n");
//  waitForResponse();

  Serial.println("Done!");
}
void waitForResponse() {
    delay(1000);
    while (mySerial.available()) {
      Serial.write(mySerial.read());
    }
    Serial.write("\n");
}
void loop() {
  if(mySerial.available() > 0){
  char command = mySerial.read();
   Serial.println(command);
//  digitalWrite(dir1PinA, HIGH);
//      digitalWrite(dir2PinA, LOW);
//      analogWrite(speedPinA, speed);
  switch(command){
    case 'A':
      digitalWrite(dir1PinA, HIGH);
      digitalWrite(dir2PinA, LOW);
      analogWrite(speedPinA, speed);
        Serial.println(command);
      break;
    case 'B':
      digitalWrite(dir1PinA, LOW);
      digitalWrite(dir2PinA, LOW);
      analogWrite(speedPinA, 0);
        Serial.println(command);
      break;
  }
  }
 
//  // set direction
//  if (1 == dir) {
//    digitalWrite(dir1PinA, LOW);
//    digitalWrite(dir2PinA, HIGH);
//  } else {
//    digitalWrite(dir1PinA, HIGH);
//    digitalWrite(dir2PinA, LOW);
//  }
//  if (millis() - time > 5000) {
//    time = millis();
//    speed = 0;
//    if (1 == dir) {
//      dir = 0;
//    } else {
//      dir =1;
//    }
//  }
}

