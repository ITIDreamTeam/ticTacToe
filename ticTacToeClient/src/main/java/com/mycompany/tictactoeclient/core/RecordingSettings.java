/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.core;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 *
 * @author Nadin
 */
public class RecordingSettings {

    private static final BooleanProperty recordingEnabled =
            new SimpleBooleanProperty(false);

    private RecordingSettings() {}

    public static BooleanProperty recordingEnabledProperty() {
        return recordingEnabled;
    }

    public static boolean isRecordingEnabled() {
        return recordingEnabled.get();
    }

    public static void setRecordingEnabled(boolean value) {
        recordingEnabled.set(value);
    }
}