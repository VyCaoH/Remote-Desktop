import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinUser.HHOOK;


import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import java.lang.ProcessBuilder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.io.IOException;
import java.net.Socket;


class AppRunning extends JPanel implements Runnable {
    private JTextArea APList;
    private JButton killApp;
    private JButton openApp;
    private JButton refreshApp;
    private JTextField pidText;
	private String myFont = "ClearGothic";
    private Socket socket;
	private JFrame frame;
	private DataInputStream dataInputStream;
	private ObjectInputStream objectInputStream;
    AppRunning ppp=this;
	HHOOK hhk = null;
	HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
	Thread thread;
	Object lock = new Object();
    public interface User32jna extends Library {
        AppRunning.User32jna INSTANCE = null;
        User32jna INSTACE = (User32jna) Native.loadLibrary("user32.dll", User32jna.class);
    }
	public AppRunning(JFrame frame, Socket socket) {
		setLayout(null);
		this.socket = socket;
		thread = new Thread(this);
		thread.start();
	}
    public static void sendApp(Socket s) throws Exception{
        //DataOutputStream cout = new DataOutputStream(s.getOutputStream());
        // cout.writeUTF(GetApplication());
        ObjectOutputStream cout=new ObjectOutputStream(s.getOutputStream());
        StringBuilder sb = new StringBuilder();
        try {
//            Run command and print to the console
            ProcessBuilder pb = new ProcessBuilder("powershell.exe", "gps", "|where {$_.MainWindowTitle }", "|select Name,Id");
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(pb.start().getInputStream()));
            String s1;
            String[][]list2=new String[100][2];
            int rowCount=0;
            s1 = stdInput.readLine();
            s1 = stdInput.readLine();
            s1 = stdInput.readLine();
            while ((s1 = stdInput.readLine()) != null) {
                // try {
                //     sb.append(s1);
                //     sb.append("\n");
                // } catch (Exception e) {
                //     e.printStackTrace();
                // }
                if(s1.equals(""))continue;
                list2[rowCount][0]=s1.substring(0,s1.indexOf(" "));
                list2[rowCount][1]=s1.substring(s1.lastIndexOf(" ")+1);
                rowCount++;
            }
            cout.writeObject(list2);
        } catch (Exception e) {
            e.printStackTrace();
            //sb = new StringBuilder("Error Table");
        }
    }
    private static String GetApplication() {
        StringBuilder sb = new StringBuilder();
        try {
//            Run command and print to the console
            ProcessBuilder pb = new ProcessBuilder("powershell.exe", "gps", "|where {$_.MainWindowTitle }", "|select Name,Id");
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(pb.start().getInputStream()));
            String s;
            while ((s = stdInput.readLine()) != null) {
                try {
                    sb.append(s);
                    sb.append("\n");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            sb = new StringBuilder("Error Table");
        }
        return sb.toString();
    }
    public static void KillApp(int processPID) {
        try {
            ProcessBuilder pb = new ProcessBuilder("powershell.exe", "Stop-Process", "-Id", Integer.toString(processPID));
            pb.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void StartApp(String processName) {
        try {
            String[] cmd = new String[]{"powershell.exe", "&",  processName, ""};
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.start();
            //Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }
}