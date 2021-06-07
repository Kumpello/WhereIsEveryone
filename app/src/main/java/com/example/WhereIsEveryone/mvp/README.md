# MVP

The app uses MVP architecture, which means Model-View-Presenter.

There is some base classes to make the development better that uses a high-level abstraction.

This document explains how does it work and how to create another activities.

# Architecture

All views (activities, fragments...) should implement Contract.View interface. There is BaseActivity and if we create an MVP Activity it should be a base class. The BaseActivity class contains the code responsible for:
- obtaining DependencyContainer from Application
- creating a proper presenter
- attaching and detaching the View dynamically

This way we don't need to do it manually in new activities.

All presenters however should implement Contract.Presenter<View> interface. Using generic parameter here allows us to create some architecture components basing on the type. BasePresenter is another base-class in our architecture. The only code it adds it code responsible for view-binding in attach/detach.

All model instances should be created in DependecyContainer file. Some of them may be singletons, some of them not.
In DependecyContainer file we will see also methods for creating presenters. Those methods always should return a new instance. Also, a method `getPresenter` is quite interesting. This method usage may be found in BaseActivity - basing on generic type and the mapping inside that method we are able to create new presenters.

# Adding new app-screen

This example will show how to add a new screen to the app using the architecture described above.
Let's say this will be "GDPRView".

1. At first we need to create the contract (View+Presenter). 

```java
import com.example.WhereIsEveryone.mvp.Contract;

// must extends Contract.View
interface GDPRView extends Contract.View {  
    void showFullGDPR();
    void nextScreen();
}

// must extends Presenter with generic type
interface GDPRPresenter extends Contract.Presenter<Contract.View> { 
    void showMoreClicked();
    void okClicked(boolean agreed);
}
```

2. Create presenter implementation:

```java
import androidx.annotation.NonNull;
import com.example.WhereIsEveryone.mvp.BasePresenter;

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

3. The last part - creating a new Activity (view implementation)

```java
import com.example.WhereIsEveryone.mvp.BaseActivity;

class GDPRActivity extends BaseActivity<GDPRPresenter> implements GDPRView {
    // Implementation here

    // We can obtain our presenter just by doing `presenter` as it's a protected property
    // in BaseActivity

    // Thanks to the BaseActivity and DependencyContainer we don't need to remember about
    // initializing the Presenter. 
}
```



