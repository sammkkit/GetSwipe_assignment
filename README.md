# 📱 Swipe Assignment — Android App

A modern Android application built using **Jetpack Compose**, designed as part of the Swipe assignment.  
The app enables users to **add, view, and manage products**, with clean UI and a scalable architecture.

---

## ✨ Features

- 🖼️ **Splash Screen** with logo animation using the Android 12+ Splash API.  
- 🆕 **Add Product** with multiple image selection and product details.  
- 📋 **Product Listing** fetched from network with offline caching.  
- 🗃️ **Room Database** integration for local storage.  
- 🔄 **Real-time Sync** between local Room DB and network.  
- 🧭 **MVVM + Clean Architecture** for better scalability and testability.  
- 🌐 **API Integration** using Retrofit & Coroutines.  
- 🧰 Fully **Jetpack Compose UI** with modern Android design patterns.

---

## 🧭 Project Architecture

presentation/
├── addProduct/ # UI + ViewModel for adding product
├── productList/ # UI + ViewModel for displaying products
├── splash/ # Splash Screen logic

data/
├── local/ # Room database & DAO
├── remote/ # Retrofit API service
├── mapper/ # Entity ↔ Domain mappers
├── repository/ # Implementation of repository pattern

domain/
├── model/ # Domain models
├── repository/ # Repository interfaces

core/
├── util/ # Utility classes (UiState, constants, etc.)

---

## 🧪 Tech Stack

- **Language:** Kotlin  
- **UI:** Jetpack Compose  
- **Architecture:** MVVM + Clean Architecture  
- **Networking:** Retrofit + OkHttp  
- **Database:** Room Persistence Library  
- **Coroutines:** For asynchronous operations  
- **Dependency Injection:** Hilt  
- **Image Picker:** Activity Result API  
- **Other:** Material 3 Components, StateFlow, DataStore (if used)

---

## 🧰 Key Functionalities

### 1. Splash Screen
- Uses the new Android 12 Splash API (`Theme.SplashScreen`)  
- Displays app logo on launch with a smooth transition to the main screen.

### 2. Add Product
- Allows adding product name, type, price, and multiple images.  
- Uses `rememberLauncherForActivityResult` for image picking.  
- Stores data in local DB and syncs with the network.

### 3. Product List
- Displays products retrieved from the network (with fallback to local Room DB).  
- Automatically reflects new additions and updates.

### 4. Offline Support
- Data is cached locally in Room.  
- On reconnecting to the internet, sync happens seamlessly.

---

## 🏗️ API Integration

- Retrofit is used for making API calls.  
- All network responses are mapped to domain models using mapper classes.  
- Proper error handling is done to provide meaningful UI feedback.

---

## 🧭 State Management

- `UiState<T>` is used for handling **Loading**, **Success**, and **Error** states in UI.  
- ViewModels expose `StateFlow` for reactive UI updates.

---

## 🧑‍💻 How to Run

1. Clone this repository:
   ```bash
   git clone https://github.com/your-username/swipe-assignment.git

2.Open the project in Android Studio (Giraffe or newer recommended).
3.Sync Gradle dependencies.
4.Run the app on an emulator or a physical device.
