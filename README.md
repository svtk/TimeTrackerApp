### Time Tracker Sample App

This app can use Firebase Authentication and Firebase Firestore database.
To run the app without creating a Firebase project, use `FakeAuthenticationProvider` and `FakeSlicesRepository`.

#### Setting up the project with Firebase:

1 Create a Firebase project with Authentication and Firestore services.
Add the corresponding `app/google-services.json` file (it's set to be automatically ignored by git).

2 (Optional) Set up local [emulators](https://firebase.google.com/docs/emulator-suite/connect_and_prototype).

3 (Optional) It's possible to [run an android phone](https://developer.android.com/training/basics/firstapp/running-app) with the local emulators.
However, to make it work on Mac OS, [set up](chrome://inspect/#devices) "Port forwarding" as described here:
[1](https://stackoverflow.com/questions/4779963/how-can-i-access-my-localhost-from-my-android-device/58834623#58834623), 
[2](https://developer.chrome.com/docs/devtools/remote-debugging/local-server/#port-forwarding).