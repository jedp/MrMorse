# J38 USB HID - CubeMX Project

As a hack, I'm adapting the HS mouse device to a keyboard. This seemed the
easiest route.

First, look for the "USB HID device FS Configuration Descriptor" and change the
declaration of the "Joystick Mouse HID" [sic] to "keyboard":

```
  0x01,         /*nInterfaceProtocol : 0=none, 1=keyboard, 2=mouse*/
```

Then replace the mouse descriptor with the keyboard descriptor:

```
__ALIGN_BEGIN static uint8_t HID_MOUSE_ReportDesc[HID_MOUSE_REPORT_DESC_SIZE]  __ALIGN_END =
{
        0x05, 0x01,                    /* USAGE_PAGE (Generic Desktop)                   */
        0x09, 0x06,                    /* USAGE (Keyboard)                               */
        0xa1, 0x01,                    /* COLLECTION (Application)                       */
        0x05, 0x07,                    /*   USAGE_PAGE (Keyboard)                        */
        0x19, 0xe0,                    /*   USAGE_MINIMUM (Keyboard LeftControl)         */
        0x29, 0xe7,                    /*   USAGE_MAXIMUM (Keyboard Right GUI)           */
        0x15, 0x00,                    /*   LOGICAL_MINIMUM (0)                          */
        0x25, 0x01,                    /*   LOGICAL_MAXIMUM (1)                          */
        0x75, 0x01,                    /*   REPORT_SIZE (1)                              */
        0x95, 0x08,                    /*   REPORT_COUNT (8)                             */
        0x81, 0x02,                    /*   INPUT (Data,Var,Abs)                         */
        0x95, 0x01,                    /*   REPORT_COUNT (1)                             */
        0x75, 0x08,                    /*   REPORT_SIZE (8)                              */
        0x81, 0x03,                    /*   INPUT (Cnst,Var,Abs)                         */
        0x95, 0x05,                    /*   REPORT_COUNT (5)                             */
        0x75, 0x01,                    /*   REPORT_SIZE (1)                              */
        0x05, 0x08,                    /*   USAGE_PAGE (LEDs)                            */
        0x19, 0x01,                    /*   USAGE_MINIMUM (Num Lock)                     */
        0x29, 0x05,                    /*   USAGE_MAXIMUM (Kana)                         */
        0x91, 0x02,                    /*   OUTPUT (Data,Var,Abs)                        */
        0x95, 0x01,                    /*   REPORT_COUNT (1)                             */
        0x75, 0x03,                    /*   REPORT_SIZE (3)                              */
        0x91, 0x03,                    /*   OUTPUT (Cnst,Var,Abs)                        */
        0x95, 0x06,                    /*   REPORT_COUNT (6)                             */
        0x75, 0x08,                    /*   REPORT_SIZE (8)                              */
        0x15, 0x00,                    /*   LOGICAL_MINIMUM (0)                          */
        0x25, 0x65,                    /*   LOGICAL_MAXIMUM (101)                        */
        0x05, 0x07,                    /*   USAGE_PAGE (Keyboard)                        */
        0x19, 0x00,                    /*   USAGE_MINIMUM (Reserved (no event indicated))*/
        0x29, 0x65,                    /*   USAGE_MAXIMUM (Keyboard Application)         */
        0x81, 0x00,                    /*   INPUT (Data,Ary,Abs)                         */
        0xc0                           /* END_COLLECTION                                 */
};
```

Finally, becuase that changes the size of the report description, the
`HID_MOUSE_REPORT_DESC_SIZE` definition must be updated:

``
#define HID_MOUSE_REPORT_DESC_SIZE    63U
```

For my key events, I'm sending and releasing the letter `q`:

```
void sendKeyDown() {
      HAL_GPIO_WritePin(LED_USR_GPIO_Port, LED_USR_Pin, GPIO_PIN_SET);

      // Send q
	  HID_report_buf[2] =  0x14;
	  USBD_HID_SendReport(&hUsbDeviceFS, HID_report_buf, 8);
}

void sendKeyUp() {
      HAL_GPIO_WritePin(LED_USR_GPIO_Port, LED_USR_Pin, GPIO_PIN_RESET);

	  // Release key
	  HID_report_buf[2] = 0;
	  USBD_HID_SendReport(&hUsbDeviceFS, HID_report_buf, 8);
}
```
