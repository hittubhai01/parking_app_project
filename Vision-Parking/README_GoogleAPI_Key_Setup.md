## 🌍 Google Maps API Key Setup

To use **Google Maps** and **Places search** functionality in the VisionPark app, you need your own API key from the **Google Cloud Console**.  
The project is configured to read this key from a `local.properties` file, which should **not be committed** to version control.

---

### 🧩 1. Obtain a Google Maps API Key

1. **Go to the Google Cloud Console:**  
   Navigate to [https://console.cloud.google.com/](https://console.cloud.google.com/).

2. **Create or Select a Project:**  
   Create a new project or select an existing one.

3. **Enable the Required APIs:**  
   You must enable three APIs for the app to function correctly.
   - In the navigation menu, go to **APIs & Services > Library**.
   - Search for and **Enable** each of the following APIs:
     - **Maps SDK for Android** (for displaying the map)
     - **Places API** (for the location search functionality)
     - **Geolocation API** (for determining the user's current location)

4. **Create an API Key:**
   - Go to **APIs & Services > Credentials**.
   - Click **Create Credentials > API key**.
   - Your new API key will be displayed — **copy this key immediately** and keep it safe.

5. **Restrict the API Key (Highly Recommended):**
   - In the list of API keys, find the one you created and click the **Edit (pencil)** icon.
   - Under **Application restrictions**, select **Android apps**.
   - Click **Add an item** and enter the following:
     - **Package name:** `com.example.visionpark`
     - **SHA-1 certificate fingerprint:**  
       Get this from your local debug keystore by running the following command in a terminal:

       ```bash
       keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
       ```

       Copy the **SHA-1** value from the output and paste it into the field.
   - Under **API restrictions**, select **Restrict key** and choose the three APIs you enabled:
     - `Maps SDK for Android`
     - `Places API`
     - `Geolocation API`
   - Click **Save**.

---

### ⚙️ 2. Add the API Key to Your Project

1. **Create the file:**  
   In the root directory of the `Vision-Parking` project, create a file named `local.properties` if it doesn't already exist.

2. **Add the key:**  
   Open the `local.properties` file and add the following line, replacing `YOUR_API_KEY_HERE` with your actual key:

   ```properties
   MAPS_API_KEY="YOUR_API_KEY_HERE"
   ```

---

### 🚀 3. Sync and Run

After adding the key, **Sync Gradle** in Android Studio.  
The app should now build and run with full **map display** and **location search** functionality.

---

> 🔒 **Note:** Never upload or share your API key publicly (e.g., GitHub, shared repos). Always keep it in your local `local.properties` file.

---

### ⚠️ Current Implementation Status (for Review)

- Currently, the **Google API key** is stored directly inside:  
  `Vision-Parking/app/src/main/AndroidManifest.xml`

  ```xml
  <meta-data
      android:name="com.google.android.geo.API_KEY"
      android:value="AIzaSyD9q-X_oGS8oggZcXHcyXhZBY-8oKbBfGg" />
  ```

- The **API key restriction** is currently **invalid or disabled**, meaning the key is not securely restricted to your app’s package and SHA-1 fingerprint.

> 🛠 **Action Recommended:**  
> Move the API key to `local.properties` and apply proper API key restrictions to prevent unauthorized usage and security vulnerabilities.
