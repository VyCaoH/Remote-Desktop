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


class ProcessRunning extends JPanel implements Runnable {
    private JTextArea APList;
    private JButton killProcess;
    private JButton openProcess;
    private JButton refreshProcess;
    private JPanel buttons;
    private JTable table;
    private JTextField pidText;
	private String myFont = "ClearGothic";
    private Socket socket;
	private JFrame frame;
	private DataInputStream dataInputStream;
	private ObjectInputStream objectInputStream;
    ProcessRunning ppp=this;
	HHOOK hhk = null;
	HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
	Thread thread;
	Object lock = new Object();
    String Processs="";
    String[][]data;
    String[][]data_new;
    String[]colName={"Name","ID"};
    public interface User32jna extends Library {
        ProcessRunning.User32jna INSTANCE = null;
        User32jna INSTACE = (User32jna) Native.loadLibrary("user32.dll", User32jna.class);
    }
	public ProcessRunning(JFrame frame, Socket socket) {
		setLayout(null);
		this.socket = socket;
		this.frame = frame;
        try {
            this.socket=socket;
            dataInputStream=new DataInputStream(socket.getInputStream());
            objectInputStream=new ObjectInputStream(socket.getInputStream());
            data=(String[][])objectInputStream.readObject();
            table=new JTable(data,colName);
            
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.toString());
        }

        buttons=new JPanel(new GridLayout(1,3));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.createHorizontalScrollBar();
        scrollPane.setBounds(20,90,600,400);
        add(scrollPane);
        
        pidText = new JTextField(25);

        killProcess = new JButton("Kill");
        killProcess.addActionListener(this::actionPerformed);
        killProcess.setFocusable(false);
        killProcess.setBounds(65,20,150,50);

        openProcess = new JButton("Open");
        openProcess.addActionListener(this::actionPerformed);
        openProcess.setFocusable(false);
        openProcess.setBounds(235,20,150,50);

        refreshProcess = new JButton("Refresh");
        refreshProcess.addActionListener(this::actionPerformed);
        refreshProcess.setFocusable(false);
        refreshProcess.setBounds(405,20,150,50);

        add(pidText);
        add(killProcess);
        add(openProcess);
        add(refreshProcess);
        System.out.println(socket.getInetAddress());
		thread = new Thread(this);
		thread.start();
	}
    public static void sendProcess(Socket s) throws Exception{
        ObjectOutputStream cout=new ObjectOutputStream(s.getOutputStream());
        try {
            ProcessBuilder pb = new ProcessBuilder("powershell.exe", "Get-Process", "|select ProcessName,Id");
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(pb.start().getInputStream()));
            String s1;
            String[][]list2=new String[400][2];
            int rowCount=0;
            s1 = stdInput.readLine();
            s1 = stdInput.readLine();
            s1 = stdInput.readLine();
            while ((s1 = stdInput.readLine()) != null) {
                if(s1.equals(""))continue;
                list2[rowCount][0]=s1.substring(0,s1.indexOf(" "));
                list2[rowCount][1]=s1.substring(s1.lastIndexOf(" ")+1);
                rowCount++;
            }
            cout.writeObject(list2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void sendRefresh(){
        try {
            DataOutputStream cout = new DataOutputStream(this.socket.getOutputStream());
            cout.writeUTF("RS");
            try {
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
    private void actionPerformed(ActionEvent e) {
        if (e.getSource() == killProcess) {
            try{
                PrintWriter writer=new PrintWriter(this.socket.getOutputStream());
                String kill= JOptionPane.showInputDialog("Enter ID: ");
                writer.println(kill);
                sendKill(Integer.parseInt(kill));
            }catch(IOException e1){
                System.out.println(e1);
        }
        }else if(e.getSource() == openProcess){
            try{
                PrintWriter writer=new PrintWriter(this.socket.getOutputStream());
                String open= JOptionPane.showInputDialog("Enter name: ");
                writer.println(open);
                sendOpen(open);
            }catch(IOException e1){
                System.out.println(e1);
            }
        }else if(e.getSource()==refreshProcess){
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