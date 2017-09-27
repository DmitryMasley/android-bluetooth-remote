#include <SoftwareSerial.h>
#include <Servo.h>

SoftwareSerial mySerial(2, 3); // RX, TX
#define BLUETOOTH_SPEED 9600
#define ROBOT_NAME "Lego Robot"

// motor B
int dir1PinB = 8;
int dir2PinB = 11;
int speedPinB = 9;



// motor A
int dir1PinA = 13;
int dir2PinA = 12;
int speedPinA = 10;

String currentSteering = "";

void setup() {;
  Serial.begin(BLUETOOTH_SPEED);
  
  pinMode(dir1PinB, OUTPUT);
  pinMode(dir2PinB, OUTPUT);
  
  pinMode(dir1PinA, OUTPUT);
  pinMode(dir2PinA, OUTPUT);
  pinMode(speedPinA, OUTPUT);
     
//  Serial.println("Starting config");
  mySerial.begin(BLUETOOTH_SPEED);

//  Serial.println("Done!");
}

void waitForResponse() {
    delay(1000);
    while (mySerial.available()) {
      Serial.write(mySerial.read());
    }
    Serial.write("\n");
}
void loop() {
  if(Serial.available() > 0){
    String command = Serial.readStringUntil('\n');
    Serial.println(command);
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
    ptr = strtok(NULL, "|");
    String angle = ptr;
    Serial.println(direction);
    Serial.println(speed);
    Serial.println(steering);
    
    int speedValue = speed.toInt();
    Serial.println(speedValue);
    if(speedValue > 0){
      if(direction.equals("1")){
        Serial.println("Go Back");
          digitalWrite(dir1PinA, LOW);
          digitalWrite(dir2PinA, HIGH);
          digitalWrite(dir1PinB, LOW);
          digitalWrite(dir2PinB, HIGH);
      }else if(direction.equals("0")){
        Serial.println("Go Froward");
          digitalWrite(dir1PinA, HIGH);
          digitalWrite(dir2PinA, LOW);    
          digitalWrite(dir1PinB, HIGH);
          digitalWrite(dir2PinB, LOW); 
      }
    
    } else {
      digitalWrite(dir1PinA, LOW);
      digitalWrite(dir2PinA, LOW);
      analogWrite(speedPinA, 0);
      digitalWrite(dir1PinB, LOW);
      digitalWrite(dir2PinB, LOW);
      analogWrite(speedPinB, 0);
    }
//      if(steering.equals("0")){
//        Serial.println("Go left");
//        if(speedValue > 0){
//          analogWrite(speedPinA, speedValue);
//          analogWrite(speedPinB, 0);
//        }
//      } else if(steering.equals("1")){
//        Serial.println("Go right");
//       if(speedValue > 0){
//          analogWrite(speedPinA, 0);
//          analogWrite(speedPinB, speedValue);
//        }
//      } else if(steering.equals("2")){
//        Serial.println("Go straight");
//        analogWrite(speedPinA, speedValue);
//        analogWrite(speedPinB, speedValue);
//      }
    int angleValue = angle.toInt();
    Serial.println(angleValue);
    if(angleValue > 0) {
       analogWrite(speedPinA, speedValue-angleValue);
       analogWrite(speedPinB, speedValue);
    }
    if(angleValue < 0) {
       analogWrite(speedPinA, speedValue);
       analogWrite(speedPinB, speedValue-angleValue);
    }
    if(angleValue == 0) {
       analogWrite(speedPinA, speedValue);
       analogWrite(speedPinB, speedValue);
    }  
}
}
