#include <LiquidCrystal.h>
#include <SoftwareSerial.h>
#include <OneWire.h>
#include <DallasTemperature.h>

LiquidCrystal lcd(12, 11, 6, 5, 4, 3); // RS -> PD12; EN -> PD11; D4 -> PD6; D5 -> PD5; D6 -> PD4; D7 -> PD3
OneWire oneWire(2); //sensor data plugged into PD2
DallasTemperature therm(&oneWire);

String celc = "Degrees C: "; // Initialize Strings for LCD
String fahr = "Degrees F: ";
String err = "    Error   ";
String noSens = "No Sensor Found";

void reqTemp();
void printLCD(String s1, String s2);
SoftwareSerial BTSerial(9, 10); // Initialize HC-06 on Arduino

void setup(void)
{
  Serial.begin(9600);
  therm.begin();
  lcd.begin(16,2);
  BTSerial.begin(9600);
  
  pinMode(7, INPUT); // Initialize Pin 7 as button
  
  reqTemp(); // Requests temperature
  
  celc = "Degrees C: " + String(therm.getTempCByIndex(0));
  fahr = "Degrees F: " + String(therm.getTempFByIndex(0));
}


void loop() {
  delay(1000); // Sets one second interval
  
  reqTemp(); // Reads the temperature, regardless of button
  while(digitalRead(7) == HIGH){ // Checks if button is pressed, which indicated the LCD should display
    if(therm.getTempCByIndex(0) == -127){ // Checks if Sensor isn't receiving data
      printLCD(err, noSens); // Prints "Error No Sensor Found" to LCD
    }
    else{
      fahr = "Degrees F: " + String(therm.getTempFByIndex(0));
      celc = "Degrees C: " + String(therm.getTempCByIndex(0));
      
      printLCD(celc,fahr); // Prints the Celcius and Fahrenheit Temperatures to the LCD
      Serial.print("\n" + String(therm.getTempCByIndex(0)));
      BTSerial.print("\n" + String(therm.getTempCByIndex(0))); // Sends data over the HC-06 device
    }
  }
  Serial.print("\n" + String(therm.getTempCByIndex(0)));
  BTSerial.print("\n" + String(therm.getTempCByIndex(0))); // Sends data over the HC-06 device
  lcd.clear();
}

void printLCD(String s1, String s2){ // Prints to LCD line by line
  lcd.setCursor(0,0); // Top line of 16x2 LCD
  lcd.print(s1);
  lcd.setCursor(0,1); // Bottom Line
  lcd.print(s2);
}

void reqTemp() { // Method to get temperatures from the DS18B20 temperature sensor
  therm.setWaitForConversion(false); // Enables asynchronous temperature requests
  therm.requestTemperatures();
  therm.setWaitForConversion(true);
}
