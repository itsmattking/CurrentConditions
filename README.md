# CurrentConditions app

## Running the app

Note that the latest stable Android Studio is required to build and run the app as Hilt relies on AS version 4.0 and higher.

### OpenWeatherMap App ID

For the app to build, a file called `apikeys.properties` must exist locally containing

`OPENWEATHER_APP_ID="<your-app-id>"`

Get this app id by signing up at openweathermap.org

## Architecture

The app is written using **MVVM** and **Clean architecture**. It contains the main **app** module as well as a **common** module that can be used when expanding to other feature modules.

* **Domain** Contains all business logic, models, use cases, and interfaces for repositories
* **Data** Contains repository implementations, services to retrieve data, and mappers to convert data models to domain models
* **Presentation** Contains view models, Android views, Fragment and Activity classes.

## Libraries used

### General
* **Kotlin Coroutines** for running tasks on background threads
* **Kotlin Flows** used for handling state change on data updates

### Network Layer

* **OKHttp** for network communication
* **Retrofit** for modeling HTTP requests
* **Moshi** for JSON serialisation

### Data layer

* **Room** for offline data cache storage

### Dependency Injection

Using **Hilt**, built on top of **Dagger** for dependency injection.

### Presentation/UI

* **AndroidViewModel** for ViewModel layer
* **Android LiveData** for observable data changes
* **Glide** for image loading
* **Navigation Component** for navigating between fragments
* **Android ViewBinding** for safe and automatic access to views

### Testing
* **JUnit4** for general unit tests
* **mockk** mocking library
* **AndroidX testing libraries** for live data and coroutines testing
* **Robolectric** for Android-specific unit testing (view models)
* **Truth** for assertions
