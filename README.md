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

Application is made by Model-View-Presenter pattern <br />
<br />
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
<br />
Each Activity, Fragment and Presenter uses their base class and uses manually made Dependency Injection. (TBD: Migrate to DI framework) <br />

    public class BaseActivity<P extends Contract.Presenter> extends AppCompatActivity implements Contract.View {

        protected P presenter;

        @SuppressWarnings("unchecked")
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.presenter = (P) getContainer().getPresenter(this);
            presenter.attachView(this);
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            presenter.detachView();
        }

        protected DependencyContainer getContainer() {
            return ((MyApplication) this.getApplication()).getContainer();
        }
    }
    
To add new app screen we need to: <br />
1. Create the contract (View+Presenter). 

```java
import com.example.whereiseveryone.mvp.Contract;

// must extends Contract.View
interface GDPRView extends Contract.View {  
    void showFullGDPR();
    void nextScreen();
}

// must extends Presenter with generic type
interface GDPRPresenter extends Contract.Presenter<GDPRView> { 
    void showMoreClicked();
    void okClicked(boolean agreed);
}
```
2. Create presenter implementation:

```java
import androidx.annotation.NonNull;
import com.example.whereiseveryone.mvp.BasePresenter;

class GDPRPresenterImpl extends BasePresenter<GDPRView> implements GDPRPresenter {
    // implementation here

    // We can access proper view just using `view` (it's protected property in BasePresenter)
}

// In dependency container we must add:
class DependencyContainer {

    // presenter definition
    @NonNull
    public GDPRPresenter getGDPRPresenter() {
        return new GDPRPresenter();
    }

    // another entry in the method...
    @SuppressWarnings("unchecked")
    public <V extends Contract.View> BasePresenter<V> getPresenter(V injector) {
        if (injector instanceof SignUpView) {
            // ugly, but it'll work
            return (BasePresenter<V>) getSingUpPresenter();
        } else if (injector instanceof LoginView) {
            return (BasePresenter<V>) getLoginPresenter();
        } 
        // NEW CODE
        else if (injector instanceof GDPRView) {
            return (BasePresenter<V>) getGDPRPresenter();
        }
        // --NEW CODE

        throw new IllegalArgumentException("no presenter for such a view");
    }    
}
```

3. Create a new Activity (view implementation)

```java
import com.example.whereiseveryone.mvp.BaseActivity;

class GDPRActivity extends BaseActivity<GDPRPresenter> implements GDPRView {
    // Implementation here

    // We can obtain our presenter just by doing `presenter` as it's a protected property
    // in BaseActivity

    // Thanks to the BaseActivity and DependencyContainer we don't need to remember about
    // initializing the Presenter. 
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
