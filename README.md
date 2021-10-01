# WhereIsEveryone
Android application for finding where your friends are

How to run application

To get this application working you will need to setup Firebase and get Google Api key, in Firebase you need Authentication and Realtime database.
https://cloud.google.com/firestore/docs/client/get-firebase
https://cloud.google.com/maps-platform
After doing so add following files:
  /app/src/main/res/values/secrets.xml
      <resources>
        <string name="server_address">HERE_YOU_NEED_TO_PUT_YOUR_FIREBASE_SERVER_ADDRES</string>
      </resources>
  /app/src/debug/res/values/google_maps_api.xml
  /app/src/release/res/values/google_maps_api.xml
      Both files as in google maps platform
