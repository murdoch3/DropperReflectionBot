package org.example;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Field;
import java.net.InetAddress;

import static org.example.ReflectionUtils.getField;
import static org.example.ReflectionUtils.invokeMethod;

public class BotKeyListener implements KeyListener {
    public ReflectionBot bot;

    public BotKeyListener(ReflectionBot bot) {
        this.bot = bot;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == '/') {
            try {

                // Send shift down.  With shift-drop enabled, clicking on an item will cause it be dropped.
                KeyEvent keyEvent = new KeyEvent(bot.applet, 0, 0, 0, KeyEvent.VK_SHIFT, KeyEvent.CHAR_UNDEFINED);
                for (KeyListener kl : bot.applet.getKeyListeners()) {
                    kl.keyPressed(keyEvent);
                }

                // Load the client's inventory interface
                Class<?> rsInterfaceClass = bot.loadClass("RSInterface");
                Field interfaceCacheField = getField(rsInterfaceClass, "interfaceCache");
                Object[] interfaceCache = (Object[]) interfaceCacheField.get(bot.applet);
                Object invInterface = interfaceCache[3214];

                // Get inventory contents
                Field invField = getField(rsInterfaceClass, "inv");
                int[] inv = (int[])invField.get(invInterface);

                Field fInterfaceHeight = getField(rsInterfaceClass, "height");
                Field fInterfaceWidth = getField(rsInterfaceClass, "width");
                Field fInterfaceSpritePadX = getField(rsInterfaceClass, "invSpritePadX");
                Field fInterfaceSpritePadY = getField(rsInterfaceClass, "invSpritePadY");
                int interfaceStartX = 569;  // int i2 = class9.childX[l1] + i;
                int interfaceStartY = 213;  // int j2 = class9.childY[l1] + l - j1;
                int invWidth = (int)fInterfaceWidth.get(invInterface);
                int invHeight = (int)fInterfaceHeight.get(invInterface);

                int timeBetweenActions = 100;

                for (int r = 0; r < invHeight; r++) {
                    for (int c = 0; c < invWidth; c++) {
                        // If there is not an item in this slot, move on
                        if (inv[r*invWidth + c] == 0) {
                            continue;
                        }

                        // Calculate item sprite location
                        int invX = interfaceStartX + c * (32 + (int)fInterfaceSpritePadX.get(invInterface));
                        int invY = interfaceStartY + r * (32 + (int)fInterfaceSpritePadY.get(invInterface));

                        // Click in the middle of the sprite
                        int clickX = invX + 16;
                        int clickY = invY + 16;

                        // Generate mouse move event to the inventory
                        MouseEvent mouseMoveEvent = new MouseEvent(bot.applet, MouseEvent.MOUSE_MOVED, 0, 0, clickX, clickY, 1, false);
                        invokeMethod(bot.gameClass.getSuperclass(), "mouseMoved", new Class[]{MouseEvent.class}, bot.applet, new Object[]{mouseMoveEvent});
                        Thread.sleep(timeBetweenActions);

                        // Mouse pressed event
                        MouseEvent mouseEvent = new MouseEvent(bot.applet, MouseEvent.MOUSE_CLICKED, 0, 0, clickX, clickY, 1, false);
                        invokeMethod(bot.gameClass.getSuperclass(), "mousePressed", new Class[]{MouseEvent.class}, bot.applet, new Object[]{mouseEvent});
                        Thread.sleep(timeBetweenActions);

                        // Mouse released event
                        MouseEvent mouseReleaseEvent = new MouseEvent(bot.applet, MouseEvent.MOUSE_RELEASED, 0, 0, clickX, clickY, 1, false);
                        invokeMethod(bot.applet.getClass().getSuperclass(), "mouseReleased", new Class[]{MouseEvent.class}, bot.applet, new Object[]{mouseReleaseEvent});
                        Thread.sleep(timeBetweenActions);
                    }
                }

                // Release shift
                keyEvent = new KeyEvent(bot.applet, 0, 0, 0, KeyEvent.VK_SHIFT, KeyEvent.CHAR_UNDEFINED);
                for (KeyListener kl : bot.applet.getKeyListeners()) {
                    kl.keyReleased(keyEvent);
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
