#include <SoftwareSerial.h>
#include <Servo.h>

SoftwareSerial mySerial(2, 3); // RX, TX
#define BLUETOOTH_SPEED 9600
#define ROBOT_NAME "Lego Robot"

// motor A
int dir1PinA = 13;
int dir2PinA = 12;
int speedPinA = 10;

String currentSteering = "";
Servo steer;

void setup() {
//  steer.attach(4);
  steer.write(90);
  Serial.begin(38400);
  
  pinMode(dir1PinA, OUTPUT);
  pinMode(dir2PinA, OUTPUT);
  pinMode(speedPinA, OUTPUT);
  
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
  String command = mySerial.readStringUntil('\n');
  char cmd[command.length()];
  char * ptr;
  Serial.print("String Length:");
  Serial.println(command.length());
  command.toCharArray(cmd, command.length());
  
  ptr = strtok(cmd,"|");
  String direction = ptr;
  ptr = strtok(NULL, "|");
  String speed = ptr;
  ptr = strtok(NULL, "|");
  String steering = ptr;
  Serial.println(direction);
  Serial.println(speed);
  Serial.println(steering);
  
  speed = speed.substring(6);
  int speedValue = speed.toInt();
  Serial.println(speedValue);
  if(speedValue > 0){
    if(direction.equals("Dir:BACK")){
      Serial.println("Go Back");
        digitalWrite(dir1PinA, LOW);
        digitalWrite(dir2PinA, HIGH);
    }else if(direction.equals("Dir:FORWARD")){
      Serial.println("Go Froward");
        digitalWrite(dir1PinA, HIGH);
        digitalWrite(dir2PinA, LOW);    
    }
    analogWrite(speedPinA, speedValue);
  } else {
    digitalWrite(dir1PinA, LOW);
    digitalWrite(dir2PinA, LOW);
    analogWrite(speedPinA, 0);
  }
  if(!currentSteering.equals(steering)){
    if(steering.equals("Steer:LEFT")){
      Serial.println("Go left");
      steer.write(20);
    } else if(steering.equals("Steer:RIGHT")){
      Serial.println("Go right");
      steer.write(160);
    } else if(steering.equals("Steer:STRAIGHT")){
      Serial.println("Go straight");
      steer.write(90);
    }
    currentSteering = steering;
  }
  Serial.println("Command:" + command);
  
  }
   
//  digitalWrite(dir1PinA, HIGH);
//      digitalWrite(dir2PinA, LOW);
//      analogWrite(speedPinA, speed);
//  switch(command){
//    case 'A':
//      digitalWrite(dir1PinA, HIGH);
//      digitalWrite(dir2PinA, LOW);
//      analogWrite(speedPinA, speed);
//        Serial.println(command);
//      break;
//    case 'B':
//      digitalWrite(dir1PinA, LOW);
//      digitalWrite(dir2PinA, LOW);
//      analogWrite(speedPinA, 0);
//        Serial.println(command);
//      break;
//    case 'L': 
//      steer.write(20);
//      break;
//    case 'R':
//      steer.write(160);
//      break;
//    case 'S':
//      steer.write(90);
//      break;
//  }
// 
  // set direction
//  if (1 == dir) {
//    steer.write(20);
//    digitalWrite(dir1PinA, LOW);
//    digitalWrite(dir2PinA, HIGH);
//  } else {
//    steer.write(160);
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

