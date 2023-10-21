package com.aicodix.rattlegram.Modes.ImageFormats;

import android.graphics.Color;

final class YuvConverter {
    static int convertToY(int color) {
        double R = Color.red(color);
        double G = Color.green(color);
        double B = Color.blue(color);
        return clamp(16.0 + (.003906 * ((65.738 * R) + (129.057 * G) + (25.064 * B))));
    }

    static int convertToU(int color) {
        double R = Color.red(color);
        double G = Color.green(color);
        double B = Color.blue(color);
        return clamp(128.0 + (.003906 * ((-37.945 * R) + (-74.494 * G) + (112.439 * B))));
    }

    static int convertToV(int color) {
        double R = Color.red(color);
        double G = Color.green(color);
        double B = Color.blue(color);
        return clamp(128.0 + (.003906 * ((112.439 * R) + (-94.154 * G) + (-18.285 * B))));
    }

    private static int clamp(double value) {
        return value < 0.0 ? 0 : (value > 255.0 ? 255 : (int) value);
    }
}