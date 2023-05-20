import javax.swing.*;
import javax.swing.event.MenuListener;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class NetworkScreenClient extends JFrame {
	private ControlPanel controlPanel = new ControlPanel();
	private String myFont = "ClearGothic";
	private JMenuBar jbar = new JMenuBar();
	private final int FRAME_WIDTH = 800;
	private final int FRAME_HEIGHT = 600;
	private Socket socket = new Socket();
	private Socket screensocket = new Socket();
	private Socket keyboardsocket = new Socket();
	private Socket appsocket = new Socket();
	private Socket shutdownsocket = new Socket();
	private Socket processsocket = new Socket();
	private JFrame jFrame = this;
	private final static int SERVER_PORT = 9999;
	private final static int SERVER_SCREEN_PORT = SERVER_PORT - 1;
	private final static int SERVER_KEYBOARD_PORT = SERVER_PORT - 2;
	private final static int SERVER_APP_PORT = SERVER_PORT - 3;
	private final static int SERVER_SHUTDOWN_PORT = SERVER_PORT - 4;
	private final static int SERVER_PROCESS_PORT = SERVER_PORT-5;
	public NetworkScreenClient() {
		setTitle("Remote Assistance Study");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		setLayout(null);
		setContentPane(controlPanel);
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setVisible(true);	
		setLocation(200, 200);
	}
	class ControlPanel extends JPanel{
		JTextField addressField = new JTextField(10);
		JButton connectBtn = new JButton("Connect");
		JButton exitBtn = new JButton("Exit");
		JButton processBtn = new JButton("Process Running");
		JButton appBtn = new JButton("App Running");
		JButton shutDownBtn = new JButton("Shut down");
		JButton screenBtn = new JButton("Screen mirror");
		JButton keyStrokeBtn = new JButton("Key stroke");
		JButton registryBtn = new JButton("Edit registry");
		public ControlPanel() {
			setLayout(null);
			
			addressField.setBounds(130, 20, 200, 50);
			connectBtn.setBounds(350, 20, 130, 50);
			exitBtn.setBounds(520, 20, 130, 50);
			processBtn.setBounds(130, 90, 250, 50);
			appBtn.setBounds(400, 90, 250, 50);
			shutDownBtn.setBounds(130, 160, 250, 50);
			screenBtn.setBounds(400, 160, 250, 50);
			keyStrokeBtn.setBounds(130, 230, 250, 50);
			registryBtn.setBounds(400, 230, 250, 50);

			processBtn.setBackground(Color.getHSBColor(274, 18, 100));
			appBtn.setBackground(Color.getHSBColor(274, 18, 100));
			shutDownBtn.setBackground(Color.getHSBColor(274, 18, 100));
			screenBtn.setBackground(Color.getHSBColor(274, 18, 100));
			keyStrokeBtn.setBackground(Color.getHSBColor(274, 18, 100));
			registryBtn.setBackground(Color.getHSBColor(274, 18, 100));

			
			addressField.setFont(new Font(myFont, Font.PLAIN, 20));
			connectBtn.setFont(new Font(myFont, Font.PLAIN, 20));
			exitBtn.setFont(new Font(myFont, Font.PLAIN, 20));
			processBtn.setFont(new Font(myFont, Font.PLAIN, 20));
			appBtn.setFont(new Font(myFont, Font.PLAIN, 20));
			shutDownBtn.setFont(new Font(myFont, Font.PLAIN, 20));
			screenBtn.setFont(new Font(myFont, Font.PLAIN, 20));
			keyStrokeBtn.setFont(new Font(myFont, Font.PLAIN, 20));
			registryBtn.setFont(new Font(myFont, Font.PLAIN, 20));

			addressField.setForeground(Color.LIGHT_GRAY);
			addressField.setText("Input IP");
			addressField.setCaretPosition(0);
			addressField.setMargin(new Insets(1, 15, 1, 15));
			addressField.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					if(addressField.getText().equals("Input IP") && addressField.getForeground() == Color.LIGHT_GRAY){
						addressField.setText("");
						addressField.setForeground(Color.BLACK);
					}
					else if(addressField.getText().equals("Connect failed")){
						addressField.setText("");
						addressField.setForeground(Color.BLACK);
					}
				}
			});
			addressField.addKeyListener(new KeyAdapter() {			
				@Override
				public void keyReleased(KeyEvent e) {
					if(addressField.getText().equals("")){
						addressField.setForeground(Color.LIGHT_GRAY);
						addressField.setText("Input IP");
						addressField.setCaretPosition(0);
					}					
				}
				
				@Override
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ENTER){
						connectBtn.doClick();
					}
					if(addressField.getText().equals("Input IP") && addressField.getForeground() == Color.LIGHT_GRAY){
						addressField.setText("");
						addressField.setForeground(Color.BLACK);
					}
					
				}
			});
			
			add(addressField);
			add(connectBtn);
			add(exitBtn);
			add(processBtn);
			add(appBtn);
			add(shutDownBtn);
			add(screenBtn);
			add(keyStrokeBtn);
			add(registryBtn);

			exitBtn.setEnabled(false);
			processBtn.setEnabled(false);
			appBtn.setEnabled(false);
			shutDownBtn.setEnabled(false);
			screenBtn.setEnabled(false);
			keyStrokeBtn.setEnabled(false);
			registryBtn.setEnabled(false);
			connectBtn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					InetSocketAddress inetAddress;
					if(addressField.getText().equals("Input IP") && addressField.getForeground() == Color.LIGHT_GRAY){
						inetAddress = new InetSocketAddress("localhost", SERVER_PORT);	
					}
					else{
					inetAddress = new InetSocketAddress(addressField.getText(), SERVER_PORT);
					}
					try {
						socket.connect(inetAddress, 1000);
					} catch (IOException e1) {
						DebugMessage.printDebugMessage(e1);
						JLabel message = new JLabel("Connect Failed");
						JDialog dialog = new JDialog(jFrame,"Alert");
						dialog.add(message);
						dialog.setSize(150,150);
						dialog.setLocation(jFrame.getLocation().x+FRAME_WIDTH/2-75,jFrame.getLocation().y+FRAME_HEIGHT/2-75);
						dialog.setVisible(true);
						socket = new Socket();
					}
					if(socket.isConnected()){
						try {
							JLabel message = new JLabel("Connect Successful");
							JDialog dialog = new JDialog(jFrame,"Alert");
							dialog.add(message);
							dialog.setSize(150,150);
							dialog.setLocation(jFrame.getLocation().x+FRAME_WIDTH/2-75,jFrame.getLocation().y+FRAME_HEIGHT/2-75);
							dialog.setVisible(true);
							System.out.println("Connected");
							Thread.sleep(1000);
							dialog.setVisible(false);
							exitBtn.setEnabled(true);
							processBtn.setEnabled(true);
							appBtn.setEnabled(true);
							shutDownBtn.setEnabled(true);
							screenBtn.setEnabled(true);
							keyStrokeBtn.setEnabled(true);
							
						} catch (InterruptedException e1) {
							DebugMessage.printDebugMessage(e1);							
						}
					}
					
				}
			});
			exitBtn.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					System.exit(1);
				}
			});
			appBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					InetSocketAddress inetAppAddress;
					if(addressField.getText().equals("Input IP") && addressField.getForeground() == Color.LIGHT_GRAY){
						inetAppAddress = new InetSocketAddress("localhost", SERVER_APP_PORT);	
					}
					else{
					inetAppAddress = new InetSocketAddress(addressField.getText(), SERVER_APP_PORT);
					}
					try {
						screensocket.connect(inetAppAddress, 1000);
					} catch (IOException e1) {
						DebugMessage.printDebugMessage(e1);
						JLabel message = new JLabel("Connect Failed");
						JDialog dialog = new JDialog(jFrame,"Alert");
						dialog.add(message);
						dialog.setSize(150,150);
						dialog.setLocation(jFrame.getLocation().x+FRAME_WIDTH/2-75,jFrame.getLocation().y+FRAME_HEIGHT/2-75);
						dialog.setVisible(true);
						appsocket = new Socket();
					}
					if(screensocket.isConnected()){
						AppRunning appPanel = new AppRunning(jFrame, screensocket);
						jFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
						jFrame.setContentPane(appPanel);
						appPanel.requestFocus();
						jFrame.revalidate();
						appPanel.requestFocus();
					}
				}
			});
			processBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					InetSocketAddress inetProcessAddress;
					if(addressField.getText().equals("Input IP") && addressField.getForeground() == Color.LIGHT_GRAY){
						inetProcessAddress = new InetSocketAddress("localhost", SERVER_PROCESS_PORT);	
					}
					else{
					inetProcessAddress = new InetSocketAddress(addressField.getText(), SERVER_PROCESS_PORT);
					}
					try {
						screensocket.connect(inetProcessAddress, 1000);
					} catch (IOException e1) {
						DebugMessage.printDebugMessage(e1);
						JLabel message = new JLabel("Connect Failed");
						JDialog dialog = new JDialog(jFrame,"Alert");
						dialog.add(message);
						dialog.setSize(150,150);
						dialog.setLocation(jFrame.getLocation().x+FRAME_WIDTH/2-75,jFrame.getLocation().y+FRAME_HEIGHT/2-75);
						dialog.setVisible(true);
						processsocket = new Socket();
					}
					if(screensocket.isConnected()){
						ProcessRunning processPanel = new ProcessRunning(jFrame, screensocket);
						jFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
						jFrame.setContentPane(processPanel);
						processPanel.requestFocus();
						jFrame.revalidate();
						processPanel.requestFocus();
					}
				}
			});
			screenBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					InetSocketAddress inetScreenAddress;
					if(addressField.getText().equals("Input IP") && addressField.getForeground() == Color.LIGHT_GRAY){
						inetScreenAddress = new InetSocketAddress("localhost", SERVER_SCREEN_PORT);	
					}
					else{
					inetScreenAddress = new InetSocketAddress(addressField.getText(), SERVER_SCREEN_PORT);
					}
					try {
						screensocket.connect(inetScreenAddress, 1000);
					} catch (IOException e1) {
						DebugMessage.printDebugMessage(e1);
						JLabel message = new JLabel("Connect Failed");
						JDialog dialog = new JDialog(jFrame,"Alert");
						dialog.add(message);
						dialog.setSize(150,150);
						dialog.setLocation(jFrame.getLocation().x+FRAME_WIDTH/2-75,jFrame.getLocation().y+FRAME_HEIGHT/2-75);
						dialog.setVisible(true);
						screensocket = new Socket();
					}
					if(screensocket.isConnected()){
						Screen screenPanel = new Screen(jFrame, screensocket);
						jFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
						jFrame.setContentPane(screenPanel);
						screenPanel.requestFocus();
						jFrame.revalidate();
						screenPanel.requestFocus();
					}
				}
			});
			keyStrokeBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					InetSocketAddress inetKeyboardAddress;
					if(addressField.getText().equals("Input IP") && addressField.getForeground() == Color.LIGHT_GRAY){
						inetKeyboardAddress = new InetSocketAddress("localhost", SERVER_KEYBOARD_PORT);	
					}
					else{
					inetKeyboardAddress = new InetSocketAddress(addressField.getText(), SERVER_KEYBOARD_PORT);
					}
					try {
						keyboardsocket.connect(inetKeyboardAddress, 1000);
					} catch (IOException e1) {
						DebugMessage.printDebugMessage(e1);
						JLabel message = new JLabel("Connect Failed");
						JDialog dialog = new JDialog(jFrame,"Alert");
						dialog.add(message);
						dialog.setSize(150,150);
						dialog.setLocation(jFrame.getLocation().x+FRAME_WIDTH/2-75,jFrame.getLocation().y+FRAME_HEIGHT/2-75);
						dialog.setVisible(true);
						keyboardsocket = new Socket();
					}
					if(keyboardsocket.isConnected()) {
						System.out.println("Connected Key stroke");
						KeyStroke screen = new KeyStroke(jFrame, keyboardsocket);
						jFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
						jFrame.setContentPane(screen);
						screen.requestFocus();
						jFrame.revalidate();
						screen.requestFocus();
					}
				}
			});
			shutDownBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    InetSocketAddress inetKeyboardAddress;
                    if(addressField.getText().equals("Input IP") && addressField.getForeground() == Color.LIGHT_GRAY){
                        inetKeyboardAddress = new InetSocketAddress("localhost", SERVER_SHUTDOWN_PORT);    
                    }
                    else{
                    inetKeyboardAddress = new InetSocketAddress(addressField.getText(), SERVER_SHUTDOWN_PORT);
                    }
                    try {
                        shutdownsocket.connect(inetKeyboardAddress, 1000);
                    } catch (IOException e1) {
                        DebugMessage.printDebugMessage(e1);
                        JLabel message = new JLabel("Connect Failed");
                        JDialog dialog = new JDialog(jFrame,"Alert");
                        dialog.add(message);
                        dialog.setSize(150,150);
                        dialog.setLocation(jFrame.getLocation().x+FRAME_WIDTH/2-75,jFrame.getLocation().y+FRAME_HEIGHT/2-75);
                        dialog.setVisible(true);
                        shutdownsocket = new Socket();
                    }
                    if(shutdownsocket.isConnected()) {
                        System.out.println("Shut down computer in 10s");
                        ShutDown shutdown = new ShutDown(jFrame, keyboardsocket);
                        jFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
                        jFrame.setContentPane(shutdown);
                        shutdown.requestFocus();
                        jFrame.revalidate();
                        shutdown.requestFocus();
                    }
                }
            });
		}		
	}
	public static void main(String[] args) {
		new NetworkScreenClient();
	}
}