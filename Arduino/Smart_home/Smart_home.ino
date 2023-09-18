#include <Arduino.h>
#include <ESP8266WiFi.h>
#include <Firebase_ESP_Client.h>
#include "addons/RTDBHelper.h"
#include "addons/TokenHelper.h"
#include <NTPClient.h>
#include <TimeLib.h>
#include <WiFiUdp.h>


// Insert your network credentials
#define WIFI_SSID "SaDiM"
#define WIFI_PASSWORD "moh55667788moh"

// Insert Firebase project API Key
#define API_KEY "3EOeLvYKIswCnEJzS15mezIzSPmAzLlhlzRY8Iel"

// Insert RTDB URL
#define DATABASE_URL "smarthome-main-default-rtdb.firebaseio.com"



// Define Firebase Data object
FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;


WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org");

unsigned long sendDataPrevMillis = 0;
bool signupOK = false;


// Replace the fixed user id with a variable
const String USER_ID = "byMnLOU0OZPemFHbvsPj3cP8x3l2";


char daysOfTheWeek[7][12] = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
char monthsOfYear[12][12] = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };

String formattedTime, dayStamp, timeStamp, autoday, withoutsec, months, finalMonth;
int monthint, monthdiff;


float h, t;
int hi, ti;

const uint8_t relayPins[] = { 16, 5, 4, 13, 12, 14 };            // Relay pin numbers
const int numRelays = sizeof(relayPins) / sizeof(relayPins[0]);  // Number of relays

int power;
int loadValues[numRelays];


void setup() {
  Serial.begin(115200);

  for (uint8_t relayPin : relayPins) {
    Serial.print("[+] Pin : ");
    Serial.print(relayPin);
    Serial.println(" Is Output");
    pinMode(relayPin, OUTPUT);
    digitalWrite(relayPin, HIGH);
  }

  Serial.println("\n[+] Try Connecting to Wi-Fi ");
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);

  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(250);
  }

  Serial.println(" [Done]");
  Serial.print("\n[+] Connected with IP : ");
  Serial.println(WiFi.localIP());

  config.api_key = "AIzaSyArxkvI9qsqsPoqqpTXnpeg17zYmvn5KAU";
  config.database_url = "smarthome-main-default-rtdb.firebaseio.com";

  if (Firebase.signUp(&config, &auth, "", "")) {
    Serial.println("[+] Sign-up to Firebase ... [Done]");
    signupOK = true;
  } else {
    Serial.printf("[!] Sign-up failed. Error: %s\n", config.signer.signupError.message.c_str());
  }

  timeClient.begin();
  timeClient.setTimeOffset(10800);  // Set time zone offset to UTC+2 (2 hours * 60 minutes * 60 seconds)
  timeClient.update();

  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);
}

void loop() {
  timeClient.update();
  if (Firebase.ready() && signupOK && (millis() - sendDataPrevMillis > 1000 || sendDataPrevMillis == 0)) {
    sendDataPrevMillis = millis();
   
    String formattedTime = timeClient.getFormattedTime();
    Serial.println(formattedTime);

    int splitT = formattedTime.indexOf("T");
    String dayStamp = formattedTime.substring(0, splitT);
    String months = dayStamp.substring(5, 7);
    int monthint = months.toInt();
    int monthdiff = monthint - 1;
    String finalMonth = String(monthsOfYear[monthdiff]);
    String timeStamp = formattedTime.substring(splitT + 1, formattedTime.length() - 1);
    String withoutsec = timeStamp.substring(0, 5);
    String autoday = String(daysOfTheWeek[timeClient.getDay()]);

    // Read Firebase data for each port

    for (int i = 0; i < numRelays; i++) {
      String daysPath = "/users/" + USER_ID + "/AutomaticOnOff/port" + String(i + 1) + "/days";
      if (Firebase.RTDB.getString(&fbdo, daysPath)) {
        String pdays = fbdo.stringData();

        String onTimePath = "/users/" + USER_ID + "/AutomaticOnOff/port" + String(i + 1) + "/ontime";
        if (Firebase.RTDB.getString(&fbdo, onTimePath)) {
          String ptime = fbdo.stringData();

          String offTimePath = "/users/" + USER_ID + "/AutomaticOnOff/port" + String(i + 1) + "/offtime";
          if (Firebase.RTDB.getString(&fbdo, offTimePath)) {
            String offtime = fbdo.stringData();

            if (pdays.indexOf(autoday) > 0) {
              if (ptime.equals(withoutsec)) {
                String portPath = "/users/" + USER_ID + "/components/port" + String(i + 1);
                Firebase.RTDB.setString(&fbdo, portPath, "1");
              }

              if (offtime.equals(withoutsec)) {
                String portPath = "/users/" + USER_ID + "/components/port" + String(i + 1);
                Firebase.RTDB.setString(&fbdo, portPath, "0");
              }
            }
          }
        }
      }
    }

    //to here


    // work fine

    for (int i = 0; i < numRelays; i++) {
      String path = "/users/" + USER_ID + "/components/port" + String(i + 1);

      if (Firebase.RTDB.getString(&fbdo, path)) {
        if (fbdo.dataType() == "string") {
          loadValues[i] = fbdo.stringData().toInt();
        } else {
          Serial.printf("[!] Invalid data type for %s. Expected string, got %s\n", path.c_str(), fbdo.dataType().c_str());
        }
      } else {
        Serial.printf("[!] Failed to read %s. Error: %s\n", path.c_str(), fbdo.errorReason().c_str());
      }
    }

    for (int i = 0; i < numRelays; i++) {
      digitalWrite(relayPins[i], power == 0 ? loadValues[i] : HIGH);
    }
  }
}
