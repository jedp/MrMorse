# Mr Morse: Morse code training app.

Work in progress.

Goal is to practice morse code using a physical telegraph key, like the J-38.

Components:
- Android app
- STM32 microcontroller
- J-38 telegraph key

The STM32 acts as a USB keyboard, converting keypresses on the J-38 to key events for the Android app.

The `android` directory contains the Android Studio project.

The `J38_USB_HID` directory contains the CubeMX project for the J-38 USB Device.

![J-38 telegraph key driving Android app via STM32 microcontroller](J-38_USB_HID.png)

