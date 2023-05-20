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
    private JPanel buttons;
    private JTable table;
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
    String apps="";
    String[][]data;
    String[][]data_new;
    String[]colName={"Name","ID"};

    public interface User32jna extends Library {
        AppRunning.User32jna INSTANCE = null;
        User32jna INSTACE = (User32jna) Native.loadLibrary("user32.dll", User32jna.class);
    }
	public AppRunning(JFrame frame, Socket socket) {
		setLayout(null);
		this.socket = socket;
		this.frame = frame;
        try {
            this.socket=socket;
            dataInputStream=new DataInputStream(socket.getInputStream());
            // apps = dataInputStream.readUTF();
            // System.out.println(apps);
            objectInputStream=new ObjectInputStream(socket.getInputStream());
            data=(String[][])objectInputStream.readObject();
            table=new JTable(data,colName);
            
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.toString());
        }
		APList = new JTextArea(apps,200, 100);
        // JScrollPane scrollPane = new JScrollPane(APList);
        // scrollPane.createHorizontalScrollBar();
        // scrollPane.setBounds(20,90,600,400);
        // add(scrollPane);

        buttons=new JPanel(new GridLayout(1,3));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.createHorizontalScrollBar();
        scrollPane.setBounds(20,90,600,400);
        add(scrollPane);

        pidText = new JTextField(25);

        killApp = new JButton("Kill");
        killApp.addActionListener(this::actionPerformed);
        killApp.setFocusable(false);
        killApp.setBounds(65,20,150,50);

        openApp = new JButton("Open");
        openApp.addActionListener(this::actionPerformed);
        openApp.setFocusable(false);
        openApp.setBounds(235,20,150,50);

        refreshApp = new JButton("Refresh");
        refreshApp.addActionListener(this::actionPerformed);
        refreshApp.setFocusable(false);
        refreshApp.setBounds(405,20,150,50);

        add(pidText);
        add(killApp);
        add(openApp);
        add(refreshApp);
        System.out.println(socket.getInetAddress());
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
    private void sendRefresh(){
        try {
            DataOutputStream cout = new DataOutputStream(this.socket.getOutputStream());
            cout.writeUTF("RS");
            try {
                // apps = dataInputStream.readUTF();
                // System.out.println(apps);
                objectInputStream=new ObjectInputStream(socket.getInputStream());
                data_new=(String[][])objectInputStream.readObject();
                for(int i=0;i<table.getRowCount();i++){
                    if(i>=data_new.length){
                        table.getModel().setValueAt("", i, 0);
                        table.getModel().setValueAt("", i, 1);
                    }
                    table.getModel().setValueAt(data_new[i][0], i, 0);
                    table.getModel().setValueAt(data_new[i][1], i, 1);
                }
            } catch (IOException e) {
                System.out.println(e.toString());
            }
            //table.removeAll();
            //table=new JTable(data,colName);

            // APList.getDocument().remove(0, APList.getDocument().getLength());
            // APList.getDocument().insertString(0, apps, null);
            // frame.revalidate();
        }catch (Exception e){
            System.out.println(e);
        }
    }
    private void sendKill(int k){
        try {
            DataOutputStream cout = new DataOutputStream(this.socket.getOutputStream());
            cout.writeUTF("KP"+ String.valueOf(k));
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private void sendOpen(String prs){
        try {
            DataOutputStream cout = new DataOutputStream(this.socket.getOutputStream());
            cout.writeUTF("OA"+ prs);
        }catch (Exception e){
            System.out.println(e);
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
    private void actionPerformed(ActionEvent e) {
        if (e.getSource() == killApp) {
            try{
                PrintWriter writer=new PrintWriter(this.socket.getOutputStream());
                String kill= JOptionPane.showInputDialog("Enter ID: ");
                writer.println(kill);
                sendKill(Integer.parseInt(kill));
            }catch(IOException e1){
                System.out.println(e1);
        }
        }else if(e.getSource() == openApp){
            try{
                PrintWriter writer=new PrintWriter(this.socket.getOutputStream());
                String open= JOptionPane.showInputDialog("Enter name: ");
                writer.println(open);
                sendOpen(open);
            }catch(IOException e1){
                System.out.println(e1);
            }
        }else if(e.getSource()==refreshApp){
            try{
                sendRefresh();
            }catch(Exception k){
                System.out.println(k);
            }
        }
    }
    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }
}