# CourtReserveScheduling-Android

An Android application for scheduling and renting sports courts with real-time data from Firebase. This project allows users to view court listings, check availability, and reserve courts for specific time slots.

---

## 📱 Features

- 🔍 **Browse Courts**: View a list of available courts with images and basic info.
- 🖼 **Court Details Page**: View detailed info including name, location, availability, and photos.
- ⏱ **Court Reservation**: Choose a start and end time to rent a court.
- ✅ **Availability Checks**: Prevent double booking by hiding rent button if the court is already reserved.
- ☁ **Firebase Integration**: Real-time court data and reservations synced using Firebase.
- 📦 **Image Loading with Glide**: Fast and efficient image loading in RecyclerViews.

---

## 🛠 Tech Stack

| Layer        | Technology        |
|--------------|-------------------|
| Language     | Java              |
| Database     | Firebase Realtime Database |
| Storage      | Firebase Storage (for images) |
| UI           | Android XML + RecyclerView |
| Image Loader | Glide             |

---

## 🚀 Setup Instructions

### Prerequisites

- Android Studio (latest version recommended)
- Firebase account
- Git

### Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/Rivaly-Kun/CourtReserveScheduling-Android.git
   cd CourtReserveScheduling-Android
````

2. **Open in Android Studio**

   * Open Android Studio
   * Choose **Open an existing project** and select the cloned folder.

3. **Connect to Firebase**

   * Go to **Tools** > **Firebase**.
   * Connect your app to Firebase and enable:

     * Realtime Database
     * Firebase Storage (optional, for images)

4. **Configure `google-services.json`**

   * Download your `google-services.json` from Firebase Console.
   * Place it in the `app/` directory.

5. **Run the App**

   * Connect your Android device or start an emulator.
   * Click **Run** ▶️

---

## 🧩 Project Structure

```
CourtReserveScheduling-Android/
├── app/
│   ├── java/com/yourpackage/
│   │   ├── activities/
│   │   │   ├── HomeActivity.java
│   │   │   ├── CourtDetailActivity.java
│   │   │   └── RentCourtActivity.java
│   │   ├── adapters/
│   │   │   └── CourtAdapter.java
│   │   └── models/
│   │       └── Court.java
│   └── res/
│       ├── layout/
│       └── drawable/
├── .gitignore
├── build.gradle
└── README.md
```

## 🙋‍♂️ Author

Developed by [Rivaly-Kun](https://github.com/Rivaly-Kun)

Feel free to contribute or report issues!
