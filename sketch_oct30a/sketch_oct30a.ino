#include <SoftwareSerial.h>
#include <Servo.h>

SoftwareSerial mySerial(2, 3); // RX, TX
#define BLUETOOTH_SPEED 9600
#define ROBOT_NAME "Lego Robot"

// motor B
int dir1PinB = 11;
int dir2PinB = 8;
int speedPinB = 9;



// motor A
int dir1PinA = 13;
int dir2PinA = 12;
int speedPinA = 5;
int servocePin = 4;

String currentSteering = "";
Servo steer;

void setup() {
  steer.attach(servocePin);
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
}

