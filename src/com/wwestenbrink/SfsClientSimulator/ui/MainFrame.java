package com.wwestenbrink.SfsClientSimulator.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame(String title) throws HeadlessException {
        super(title);
        setUndecorated(true);
        setVisible(true);
        setLocationRelativeTo(null);
    }
}
