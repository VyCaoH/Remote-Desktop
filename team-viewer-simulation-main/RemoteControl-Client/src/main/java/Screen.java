import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinUser.HHOOK;


import org.xerial.snappy.Snappy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.*;
import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

import javax.imageio.ImageIO;

public class Screen extends JPanel implements Runnable{
    private String myFont = "ClearGothic";
    private Socket socket;
    private BufferedImage screenImage;
    private JFrame frame;
    private JLabel FPSlabel;
    private int FPScount = 0;
	private BufferedImage image;
	private BufferedImage screenShotImage;
    private DataInputStream dataInputStream;
    private ObjectInputStream objectInputStream;
    private int screen_Width = 1920;
	private int screen_Height = 1080;
    private int image_Width = 1280;
    private int image_Height = 720;
    private byte imageByte2[] = new byte[6220800];
    private Boolean isCompress = true;
    private Vector<byte[]> imgVec = new Vector<>();
    Screen ppp = this;
    User32 lib = User32.INSTANCE;
	User32jna u32 = User32jna.INSTANCE;
    HHOOK hhk = null;
	HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
	Thread thread;
	Object lock = new Object();
    public interface User32jna extends Library {
        Screen.User32jna INSTANCE = null;
        User32jna INSTACE = (User32jna) Native.loadLibrary("user32.dll", User32jna.class);
    }

    public Screen(JFrame frame, Socket socket){
        setLayout(null);
        this.socket = socket;
        this.frame = frame;
		FPSlabel = new JLabel("FPS : " + Integer.toString(FPScount));
		FPSlabel.setFont(new Font(myFont, Font.BOLD, 20));
		FPSlabel.setBounds(20, 390, 100, 50);
		add(FPSlabel);
		JButton screenShotBtn = new JButton("Screen shot");
		screenShotBtn.setBounds(550, 20, 150, 50);
		screenShotBtn.setFont(new Font(myFont, Font.PLAIN, 20));
		add(screenShotBtn);
        screenShotBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				screenShotImage = image;
				try {
					final JFileChooser fc = new JFileChooser();
					fc.setDialogTitle("Save screenshot");
					fc.showSaveDialog(screenShotBtn);
					File saveFile = fc.getSelectedFile();
					ImageIO.write(screenShotImage,"png",saveFile);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
        System.out.println(socket.getInetAddress());
        try {
            setLayout(null);
			socket.setTcpNoDelay(true);
            dataInputStream = new DataInputStream(socket.getInputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e){
            DebugMessage.printDebugMessage(e);
        }
        thread = new Thread(this);
        thread.start();
        FPSCheckThread fpsCheckThread = new FPSCheckThread();
        fpsCheckThread.start();
        showThread ss = new showThread();
        ss.start();
    }
    public void run(){
        try {
            screen_Width = dataInputStream.readInt();
            System.out.println(screen_Width);
            screen_Height = dataInputStream.readInt();
            image_Width = dataInputStream.readInt();
            image_Height = dataInputStream.readInt();
            isCompress = dataInputStream.readBoolean();
        } catch (IOException e1) {
            DebugMessage.printDebugMessage(e1);
        }
        while (true) {
            try {
                if (dataInputStream.available() > 0) {
                    int length = 0;
                    if (isCompress) {
                        length = dataInputStream.readInt();
                        byte imageByte[] = new byte[length];
                        dataInputStream.readFully(imageByte, 0, length);
                        imgVec.addElement(imageByte);
                        if (imgVec.size() > 1) {
                            synchronized (lock) {
                                lock.wait();
                            }
                        }
                    }
                    else {
                        length = dataInputStream.readInt();
                        dataInputStream.readFully(imageByte2, 0, length);
                        screenImage = new BufferedImage(image_Width, image_Height,BufferedImage.TYPE_3BYTE_BGR);
                        screenImage.setData(Raster.createRaster(screenImage.getSampleModel(),
                            new DataBufferByte(imageByte2, imageByte2.length), new Point()));
                    }
                }
            } catch (Exception e) {
                DebugMessage.printDebugMessage(e);
            }
        }
    }
    class showThread extends Thread {
        byte imageByte[];
        byte uncompressImageByte[];
        public void run() {
            while (true) {
                try {
                    imageByte = imgVec.get(0);
                    uncompressImageByte = Snappy.uncompress(imageByte);
                    if (imgVec.size() > 0){
                        imgVec.remove(0);
                    }
                    screenImage = new BufferedImage(image_Width, image_Height, BufferedImage.TYPE_3BYTE_BGR);
                    screenImage.setData(Raster.createRaster(screenImage.getSampleModel(),
							new DataBufferByte(uncompressImageByte, uncompressImageByte.length), new Point()));	
					if (imgVec.size() == 1) {
						synchronized (lock) {
								lock.notify();						
						}
					}
					if(screenImage != null){
						image = screenImage;
						FPScount++;
						repaint();
					}		
                } catch (Exception e) {

                }
            }
        }
    }
    class FPSCheckThread extends Thread {
        public void run() {
            while (true) {
                try {
                    sleep(1000);
                    FPSlabel.setText("FPS: " + Integer.toString(FPScount));
                    FPScount = 0;
                } catch (InterruptedException e) {

                }
            }
        }
    }
    @Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 20, 20, Math.round(getWidth()/3*2), Math.round(getHeight()/3*2), this);
	}
}