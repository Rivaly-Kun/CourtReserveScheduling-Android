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

---

## 📸 Screenshots


![Screenshot_2025-05-05-21-45-00-12_4b4823c39307d6eeb2162938cfb4120d](https://github.com/user-attachments/assets/8f76c4d8-a35e-47fc-bf51-dd131a237ed9)
![Screenshot_2025-05-05-21-44-56-28_4b4823c39307d6eeb2162938cfb4120d](https://github.com/user-attachments/assets/407643aa-eaba-49c8-9078-24f84524e398)
![Screenshot_2025-05-05-21-44-49-98_4b4823c39307d6eeb2162938cfb4120d](https://github.com/user-attachments/assets/0f35be36-ffbb-490a-aa2c-a4279626c17d)
![Screenshot_2025-05-05-21-44-44-82_4b4823c39307d6eeb2162938cfb4120d](https://github.com/user-attachments/assets/65794a41-6cae-42da-97e2-9b102de0117c)
![Screenshot_2025-05-05-21-44-40-49_4b4823c39307d6eeb2162938cfb4120d](https://github.com/user-attachments/assets/6ad14e09-37cc-44ee-b097-575cf779ded3)
![Screenshot_2025-05-05-21-44-31-94_4b4823c39307d6eeb2162938cfb4120d](https://github.com/user-attachments/assets/33834493-5686-482b-abc9-73304a5fd43e)


---

## 🙋‍♂️ Author

Developed by [Rivaly-Kun](https://github.com/Rivaly-Kun)

Feel free to contribute or report issues!
