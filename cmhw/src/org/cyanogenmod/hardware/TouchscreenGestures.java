/*
 * Copyright (C) 2016 The CyanogenMod Project
 * Copyright (C) 2017 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cyanogenmod.hardware;

import org.cyanogenmod.internal.util.FileUtils;

import cyanogenmod.hardware.TouchscreenGesture;

/**
 * Touchscreen gestures API
 *
 * A device may implement several touchscreen gestures for use while
 * the display is turned off, such as drawing alphabets and shapes.
 * These gestures can be interpreted by userspace to activate certain
 * actions and launch certain apps, such as to skip music tracks,
 * to turn on the flashlight, or to launch the camera app.
 *
 * This *should always* be supported by the hardware directly.
 * A lot of recent touch controllers have a firmware option for this.
 *
 * This API provides support for enumerating the gestures
 * supported by the touchscreen.
 */
public class TouchscreenGestures {
    private static final String TAG = "TouchscreenGestures";
    private static final String GESTURE_PATH = "/sys/touch_screen/easy_wakeup_gesture";

    private static int[] ALL_GESTURE_MASKS = {
            0x080,  // Gesture C
            0x100,  // Gesture E
            0x200,  // Gesture M
            0x400   // Gesture W
    };

    // Id, name, keycode
    private static final TouchscreenGesture[] TOUCHSCREEN_GESTURES = {
            new TouchscreenGesture(0, "Letter C", 66),
            new TouchscreenGesture(1, "Letter E", 67),
            new TouchscreenGesture(2, "Letter M", 68),
            new TouchscreenGesture(3, "Letter W", 87),
    };

    /**
     * Whether device supports touchscreen gestures
     *
     * @return boolean Supported devices must return always true
     */
    public static boolean isSupported() {
        return FileUtils.isFileWritable(GESTURE_PATH) &&
                FileUtils.isFileReadable(GESTURE_PATH);
    }

    /*
     * Get the list of available gestures. A mode has an integer
     * identifier and a string name.
     *
     * It is the responsibility of the upper layers to
     * map the name to a human-readable format or perform translation.
     */
    public static TouchscreenGesture[] getAvailableGestures() {
        return TOUCHSCREEN_GESTURES;
    }

    /**
     * This method allows to set the activation status of a gesture
     *
     * @param gesture The gesture to be activated
     *        state   The new activation status of the gesture
     * @return boolean Must be false if gesture is not supported
     *         or the operation failed; true in any other case.
     */
    public static boolean setGestureEnabled(
            final TouchscreenGesture gesture, final boolean state) {

        String line = FileUtils.readOneLine(GESTURE_PATH);
        int gestureMode = Integer.parseInt(line.substring(2), 16);

        if (!state) {
            gestureMode = gestureMode & ~ALL_GESTURE_MASKS[gesture.id];
        } else {
            gestureMode = gestureMode | ALL_GESTURE_MASKS[gesture.id];
        }

        return FileUtils.writeLine(GESTURE_PATH, String.valueOf(gestureMode));
    }
}
