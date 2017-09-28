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
      //turn on move
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
    int angleValue = angle.toInt();
    Serial.println(angleValue);
    if(speedValue > 0) {
      int ASpeed = speedValue;
      int BSpeed = speedValue;
      if(angleValue > 0) {
        BSpeed = speedValue - (int) (angleValue * speedValue / 100);
      }
      if(angleValue < 0) {
        ASpeed = speedValue - (int) ((-angleValue) * speedValue / 100);
      }
      Serial.println("ASpeed: ");
      Serial.println(ASpeed);
      Serial.println("BSpeed: ");
      Serial.println(BSpeed);
      analogWrite(speedPinA, ASpeed);
      analogWrite(speedPinB, BSpeed);  
    } else {
      // turn around
      int ASpeed;
      int BSpeed;
      if(angleValue > 0) {
        BSpeed = (int) (angleValue * 255 / 100);  
        digitalWrite(dir1PinA, HIGH);
        digitalWrite(dir2PinA, LOW);    
        digitalWrite(dir1PinB, LOW);
        digitalWrite(dir2PinB, HIGH);    
      }
      if(angleValue < 0) {
        BSpeed = (int) ((-angleValue) * 255 / 100);
        digitalWrite(dir1PinA, LOW);
        digitalWrite(dir2PinA, HIGH);    
        digitalWrite(dir1PinB, HIGH);
        digitalWrite(dir2PinB, LOW);   
      }
      ASpeed = BSpeed;
      analogWrite(speedPinA, ASpeed);
      analogWrite(speedPinB, BSpeed);  
    }
}
}
