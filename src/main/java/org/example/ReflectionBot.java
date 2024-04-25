package org.example;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLClassLoader;

import static org.example.ReflectionUtils.*;

public class ReflectionBot {
    public Class<?> gameClass;
    public ClassLoader cl;
    public Applet applet;

    public ReflectionBot() throws Exception {
        // Load the client jar
        String pathToClientJar = "client-1.0-jar-with-dependencies.jar";
        File file = new File(pathToClientJar);
        cl = new URLClassLoader(new URL[] {file.toURI().toURL() });

        // Load Game class
        gameClass = cl.loadClass("Game");
        Constructor<?> gameClassInit = gameClass.getDeclaredConstructor();
        gameClassInit.setAccessible(true);  // turn off access checking
        applet = (Applet)gameClassInit.newInstance();

        JFrame frame = new JFrame("Bot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(765+8, 503+28));
        frame.setLocationRelativeTo(null);
        frame.add(applet);
        frame.setVisible(true);

        // Recreating 2006rebotted's main class
        setField(gameClass, "nodeID", applet, 10);
        setField(gameClass, "portOff", applet, 0);
        invokeMethod(gameClass, "setHighMem", new Class[]{}, applet, new Object[]{});
        setField(gameClass, "isMembers", applet, true);

        // How do we ensure that this version of signLink is the one that is being used?
        Class<?> signLink = cl.loadClass("Signlink");
        setField(signLink, "storeid", signLink, 32);
        invokeMethod(signLink, "startpriv", new Class[]{InetAddress.class}, signLink, new Object[]{InetAddress.getLocalHost()});

        // calling this RSApplet method
        invokeMethod(gameClass.getSuperclass(), "initClientFrame", new Class[]{int.class, int.class}, applet, new Object[]{503, 765});

        BotKeyListener botKeyListener = new BotKeyListener(this);
        invokeMethod(Component.class, "addKeyListener", new Class[]{KeyListener.class}, applet, new Object[]{botKeyListener});
    }

    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return cl.loadClass(name);
    }

    public Object getGameField(String fieldName) throws NoSuchFieldException, IllegalAccessException {
        return getField(gameClass, fieldName).get(applet);
    }
}
