# WhereIsEveryone
Android application for finding where your friends are


## How to run application

To get this application working you will need to setup Firebase and get Google Api key, in Firebase you need Authentication and Realtime database. <br />
* https://cloud.google.com/firestore/docs/client/get-firebase
* https://developers.google.com/maps/documentation/android-sdk/get-api-key <br />
After doing so add following files: <br />
  * /app/src/main/res/values/secrets.xml <br />
  ```xml
      <resources> <br />
        <string name="server_address">YOUR_FIREBASE_SERVER_ADDRES</string> <br />
      </resources> <br />
  ```
  * /app/src/debug/res/values/google_maps_api.xml <br />
  * /app/src/release/res/values/google_maps_api.xml <br />
  ```xml
      <resources>
        <string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">GOOGLE_MAPS_API_KEY</string>
      </resources> 
  ```

## How to use application
On first run of application you need to make account and log in, after that you get to map screen <br />
![MapView](https://i.imgur.com/XkdnQANm.png) <br />
Green arrow on map represents your location, red arrows are your friends. When you click on marker of your friend his nick will be displayed.
Button on right bottom corner of map centers map on you and on bottom of screen you see navigation bar, settings are yet to be done. <br />
![FriendsView](https://i.imgur.com/WTU2F3fm.png) <br />
Minus sign next to friends email is used to remove him from your friends base.
After clicking on Add Friend button you get to window where you need to type email of your friend. <br />
![AddFriendDialog](https://i.imgur.com/7KAAMTRm.png) <br />
After clicking on Change Nick button you get to window where you can change your nick. <br />
![ChangeNickDialog](https://i.imgur.com/7CPWVnOm.png) 

## Architecture

Application is made by Model-View-Presenter pattern, and uses manually made Dependency Injection. <br />
<br />
examples:
SignUpPresenterImpl.java <br />
```java
    public void signUpButtonClicked(String email, String password) {
        if (isNullOrEmpty(password) || isNullOrEmpty(email)) {
            return;
        }

        view.showProgress(); // Call to view

        loginService.getEmailAndPassword(email, password); //Call to model

        loginService.signUp(value -> {
            if (value.getError() != null) {
                view.showError(R.string.signup_failed);
                return;
            }

            view.showSuccess();
        });

    }
```    



## Tech stack
 
* Java
* Gradle
* Firebase
* GoogleMaps
