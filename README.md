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

### MVP
 
The app is made by Model-View-Presenter pattern. 
Using base classes, like `BasePresenter`, `BaseActivity`, `BaseFragment` allows managing attaching/detaching view automatically (basing on Component Life Cycle).
 
[//]: # (reference to another md, realtive path)
For more details please see: [MVP Readme File](app/src/main/java/com/example/whereiseveryone/mvp/README.md)
 
### Dependency Injection
 
All classes follows DI pattern. For now, a simple container is defined:
```java
public class DependencyContainer {
    private final Application application;
 
    // example singleton definition
    private LoginService loginService;
 
    // example singleton creation
    public synchronized getLoginService() {
        if (loginService == null) {
            loginService = new LoginServiceImpl();
        }
 
        return loginService;
    }
 
    // factory-method:
    public MainPreenter getMainPresenter() {
        return new MainPresenter();
    } 
 
    // generic factory method for presenter - ugly;
    // will be removed on migration to DI Framework
    public <V extends Contract.View> BasePresenter<V> getPresenter(V injector) throw IllegalArgumentException {
        Context context;
        // obtain context...
 
        if (injector instanceof SignUpView) {
            return (BasePresenter<V>) getSingUpPresenter(
                getDatabaseReference(context);
            );
        }
    }
}
```

**Using this solution was a stub for fast development. It will be migrated to a DI Framework like [Hilt](https://developer.android.com/training/dependency-injection/hilt-android).**
 
## Firebase Database Scheme

 ![DatabaseScheme](https://i.imgur.com/myrCPRY.png) 
 
## TODO
 
There are a few features that will be implemented/added in the future.
 
#### Features
- [ ] Screen for navigation by azimuth to find individual friends
- [ ] Add settings for map screen
- [ ] Personal avatars for friends on map screen
- [ ] Marking place on map where you will be in near future
- [ ] And many more!
 
#### Code:
- [ ] Migrate to DI Framework
- [ ] Extract interactions with the database to own class
- [ ] Add CI (via GitHub actions)
- [ ] **Greater test coverage**
- [ ] Stable (production ready) configuration for Google Cloud Services
- [ ] Deploy config (key-chain, proguard rules etc.)


## Tech stack
 
* Java
* Gradle
* Firebase
* GoogleMaps
