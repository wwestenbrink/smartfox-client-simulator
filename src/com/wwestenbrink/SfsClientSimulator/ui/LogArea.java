package com.wwestenbrink.SfsClientSimulator.ui;

import com.wwestenbrink.SfsClientSimulator.log.Logger;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LogArea extends JTextArea implements Logger {
    public void log(String msg) {
        String time = new SimpleDateFormat("HH:mm:ss.SSS").format(Calendar.getInstance().getTime());
        append(time + " " + msg);
    }
}
