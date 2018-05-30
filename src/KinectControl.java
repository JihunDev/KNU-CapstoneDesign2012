import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Vector;
 
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.bluetooth.BluetoothStateException;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import java.awt.image.*;
import java.awt.geom.*;
import java.awt.color.*;
import java.text.DecimalFormat;
import java.io.*;
import javax.imageio.*;
import java.util.*;

import org.OpenNI.*;

import java.nio.ShortBuffer;

public class KinectControl extends JFrame implements ActionListener, MouseListener, MouseMotionListener, HandleListener {

	public BTControl btControl = null;
	JPanel p1, cp, pb;
	JoystickPanel jp;
	TrackerPanel tp;
	JButton resumebt, destroybt, connectbt;	
	ImageIcon icon1[], icon2[];
//	JButton uplbt, upbt, uprbt, lbt, stopbt, rbt, downlbt, downbt, downrbt;
	JButton buts[];
	
	String iconname1[] = {
    "cont_01.jpg",
    "cont_02.jpg",
    "cont_03.jpg",
    "cont_04.jpg",
    "blank.jpg",    
    "cont_05.jpg",
    "cont_06.jpg",
    "cont_07.jpg",
    "cont_08.jpg"
  };
  String iconname2[] = {
    "cont_01_1.jpg",
    "cont_02_1.jpg",
    "cont_03_1.jpg",
    "cont_04_1.jpg",
    "blank.jpg",        
    "cont_05_1.jpg",
    "cont_06_1.jpg",
    "cont_07_1.jpg",
    "cont_08_1.jpg"
  };

	//JLabel txtX, txtY;
	private int state = 0, start = 0;
	Handle mHandle;
  private byte[] sendBuf_byte = new byte[22];    
	private byte[] readBuf_byte = new byte[32];	
	private byte[] readBuf = new byte[32];	
	private int input_counter = 0, flag=0;   
  int cnt=0;	
	
	
	public KinectControl() {
	    super("KinectControl");
	    setLayout(new BorderLayout());
	    
	    mHandle = new Handle();
	    mHandle.addHandleListener(this);
	    
	    p1 = new JPanel(new GridLayout(3, 3) );
	    cp = new JPanel(new GridLayout(1, 2) );
      jp = new JoystickPanel( );	    
      pb = new JPanel(new GridLayout(1, 3) );      
      //p3 = new JPanel(new GridLayout(1, 4) );
      tp = new TrackerPanel();
	    
	    //txtX = new JLabel("stopped", JLabel.CENTER);
	    //txtY = new JLabel("stopped", JLabel.CENTER);
	    
	    connectbt = new JButton("Connect");
	    //connectbt.setBackground(Color.YELLOW);
	    resumebt = new JButton("Resume");
	    //resumebt.setBackground(Color.YELLOW);
	    destroybt = new JButton("Destroy");	  
	    //destroybt.setBackground(Color.YELLOW);  
	    
	    icon1 = new ImageIcon[9];
	    icon2 = new ImageIcon[9];
	    buts = new JButton[9];
	    
	    for(int i=0; i < icon1.length; i++) {
	      icon1[i] = new ImageIcon("./img/"+iconname1[i]);
	      icon2[i] = new ImageIcon("./img/"+iconname2[i]);	      
	      buts[i] = new JButton(icon1[i]);
	      buts[i].addMouseListener(this);
	      p1.add(buts[i]);
	    }
/*
      uplbt = new JButton("UpLeft");
      upbt = new JButton("Up");
      uprbt = new JButton("UpRight");
      lbt = new JButton("Left");
      stopbt = new JButton(".");
      rbt = new JButton("Right");
      downlbt = new JButton("DownLeft");
      downbt = new JButton("Down");
      downrbt = new JButton("DownRight");
*/      
      
      connectbt.addActionListener(this);
      resumebt.addActionListener(this);
      destroybt.addActionListener(this);
      
      //uplbt.addActionListener(this);
      //upbt.addActionListener(this);
      //uprbt.addActionListener(this);
      //lbt.addActionListener(this);
      //stopbt.addActionListener(this);
      //rbt.addActionListener(this);
      //downlbt.addActionListener(this);
      //downbt.addActionListener(this);
      //downrbt.addActionListener(this);
/*      
      uplbt.addMouseListener(this);
      upbt.addMouseListener(this);
      uprbt.addMouseListener(this);
      lbt.addMouseListener(this);
      stopbt.addMouseListener(this);
      rbt.addMouseListener(this);
      downlbt.addMouseListener(this);
      downbt.addMouseListener(this);
      downrbt.addMouseListener(this);
*/      
      
	    Container c = this.getContentPane();
	    c.add(cp, BorderLayout.SOUTH);
	    c.add(tp, BorderLayout.CENTER);
      c.add(pb, BorderLayout.NORTH);	    

      cp.add(p1);
	    cp.add(jp);	    
	    
	    pb.add(connectbt);
	    pb.add(resumebt);
	    pb.add(destroybt);
/*	    
      p1.add(uplbt);
      p1.add(upbt);
      p1.add(uprbt);
      p1.add(lbt);
      p1.add(stopbt);
      p1.add(rbt);
      p1.add(downlbt);
      p1.add(downbt);
      p1.add(downrbt);
*/      
      //p3.add(new JLabel("txtX", JLabel.CENTER));
      //p3.add(txtX);
      //p3.add(new JLabel("txtY", JLabel.CENTER));
      //p3.add(txtY);
      
      onConnect();

	}

  public void handleEvent(HandleEvent e) {
      //System.out.println("K-"+e.msg);
      readBuf = e.msg;
      
				//CheckData();
				if (cnt % 2 == 0) {
          if (state == 5) {  // mState == 5
						//Lintracer();
						;
					}
				}
				cnt++;
  }
		
  	public void CheckData() {
  		int rx_check_sum;
  		
 			//System.out.println("readBuf[0]=" + readBuf[0] + " " + "readBuf[31]=" + readBuf[31] + " " + "readBuf[1]=" + readBuf[1]);
  			  		
  		if ((readBuf[0] == 2) && (readBuf[31] == 3) && (readBuf[1] == 31)) {
  			rx_check_sum = readBuf[0];
  			rx_check_sum = rx_check_sum + readBuf[1];
  			rx_check_sum = rx_check_sum + readBuf[3];
  			rx_check_sum = rx_check_sum + readBuf[4];
  			rx_check_sum = rx_check_sum + readBuf[5];
  			rx_check_sum = rx_check_sum + readBuf[6];
  			rx_check_sum = rx_check_sum + readBuf[7];
  			rx_check_sum = rx_check_sum + readBuf[8];
  			rx_check_sum = rx_check_sum + readBuf[9];
  			rx_check_sum = rx_check_sum + readBuf[10];
  			rx_check_sum = rx_check_sum + readBuf[11];
  			rx_check_sum = rx_check_sum + readBuf[12];
  			rx_check_sum = rx_check_sum + readBuf[13];
  			rx_check_sum = rx_check_sum + readBuf[14];

				if (cnt % 5 == 0) {
    			for(int i=0; i < 7; i++) {
      			System.out.print("IR[" + (i+1)+ "]=" + readBuf[8+i] + " ");
    			}
    			System.out.println();
  			}
  			
  			rx_check_sum = rx_check_sum + readBuf[15];
  			rx_check_sum = rx_check_sum + readBuf[16];
  			rx_check_sum = rx_check_sum + readBuf[17];
  			rx_check_sum = rx_check_sum + readBuf[18];
  			rx_check_sum = rx_check_sum + readBuf[19];
  			rx_check_sum = rx_check_sum + readBuf[20];
  			rx_check_sum = rx_check_sum + readBuf[21];
  			rx_check_sum = rx_check_sum + readBuf[22];
  			rx_check_sum = rx_check_sum + readBuf[23];
  			rx_check_sum = rx_check_sum + readBuf[24];
  			rx_check_sum = rx_check_sum + readBuf[25];
  			rx_check_sum = rx_check_sum + readBuf[26];
  			rx_check_sum = rx_check_sum + readBuf[27];
  			rx_check_sum = rx_check_sum + readBuf[28];
  			rx_check_sum = rx_check_sum + readBuf[29];
  			rx_check_sum = rx_check_sum + readBuf[30];
  			rx_check_sum = rx_check_sum + readBuf[31];
  
  			rx_check_sum = rx_check_sum % 256;
  
  			if (rx_check_sum == readBuf[2]) {
  				readBuf_byte[5] = (byte) PSDADC(readBuf[5]);
  				readBuf_byte[6] = (byte) PSDADC(readBuf[6]);
  				readBuf_byte[7] = (byte) PSDADC(readBuf[7]);
  				readBuf_byte[8] = readBuf[8];
  				readBuf_byte[9] = readBuf[9];
  				readBuf_byte[10] = readBuf[10];
  				readBuf_byte[11] = readBuf[11];
  				readBuf_byte[12] = readBuf[12];
  				readBuf_byte[13] = readBuf[13];
  				readBuf_byte[14] = readBuf[14];
  				readBuf_byte[19] = readBuf[19];
  				readBuf_byte[20] = readBuf[20];
  				readBuf_byte[21] = readBuf[21];
  				readBuf_byte[22] = readBuf[22];
  
  				input_counter = input_counter + 1;
  			}
  		}
  	}

    public void Lintracer() {
      //0~255(검은색 :30이하, 흰색:90이상
    	//	sendBuf_byte[6] = 100; // right wheel
     	//	sendBuf_byte[5] = 70;  // left wheel      
    	if (readBuf_byte[8] < 10 && readBuf_byte[9] < 10  // black
    			&& readBuf_byte[10] < 10 && readBuf_byte[11] < 10
    			&& readBuf_byte[12] < 10 && readBuf_byte[13] < 10
    			&& readBuf_byte[14] < 10) {
    		sendBuf_byte[6] = 0;
    		sendBuf_byte[5] = 0;
    	} else if (readBuf_byte[10] < 10 && readBuf_byte[11] < 10
    			&& readBuf_byte[12] < 10) {
    		sendBuf_byte[6] = 10;
    		sendBuf_byte[5] = 10;
    	} else if (readBuf_byte[9] < 10 && readBuf_byte[10] < 10) {
    		sendBuf_byte[6] = 10;
    		sendBuf_byte[5] = (byte) (10 + 128);
    	} else if (readBuf_byte[12] < 10 && readBuf_byte[13] < 10) {
    		sendBuf_byte[6] = (byte) (10 + 128);
    		sendBuf_byte[5] = 10;
    	} else if (readBuf_byte[8] < 10) {
    		sendBuf_byte[6] = 10;
    		sendBuf_byte[5] = (byte) (10 + 128);
    	} else if (readBuf_byte[9] < 10) {
    		sendBuf_byte[6] = 10;
    		sendBuf_byte[5] = (byte) (10 + 128);
    	} else if (readBuf_byte[10] < 10) {
    		sendBuf_byte[6] = 10;
    		sendBuf_byte[5] = 10;
    	} else if (readBuf_byte[11] < 10) {
    		sendBuf_byte[6] = 10;
    		sendBuf_byte[5] = 10;
    	} else if (readBuf_byte[12] < 10) {
    		sendBuf_byte[6] = 10;
    		sendBuf_byte[5] = 10;
    	} else if (readBuf_byte[13] < 10) {
    		sendBuf_byte[6] = (byte) (10 + 128);
    		sendBuf_byte[5] = 10;
    	} else if (readBuf_byte[14] < 10) {
    		sendBuf_byte[6] = (byte) (10 + 128);
    		sendBuf_byte[5] = 10;
    	} else {
    		sendBuf_byte[6] = 0;
    		sendBuf_byte[5] = 0;
    	}
    	sendByte(sendBuf_byte);
  }
	
	public void actionPerformed(ActionEvent e) {
	}
	
	public void mouseClicked(MouseEvent e) {	  
	}
	public void mouseEntered(MouseEvent e)  {
 	}
	public void mouseExited(MouseEvent e)  {
	}
	public void mousePressed(MouseEvent e)  {
	  
	  if(e.getSource() == buts[0]) {
	    buts[0].setIcon(icon2[0]);
	    //System.out.println(e.toString()); // You can use e.getID();  MOUSE_PRESSED  MOUSE_RELEASED
	    if (flag == 0) {
    		sendBuf_byte[6] = 100; // right wheel
    		sendBuf_byte[5] = 70;  // left wheel
    		sendByte(sendBuf_byte); 
    		flag = 1;
			}
	  }
	  else if(e.getSource() == buts[1]) {
	    buts[1].setIcon(icon2[1]);	    
	    if (flag == 0) {
    		sendBuf_byte[6] = 100;
    		sendBuf_byte[5] = 100;
    		sendByte(sendBuf_byte); 
    		flag = 1;
			}
	  }
	  else if(e.getSource() == buts[2]) {
	    buts[2].setIcon(icon2[2]);	    	    
	    if (flag == 0) {
    		sendBuf_byte[6] = 70;
    		sendBuf_byte[5] = 100;
    		sendByte(sendBuf_byte); 
    		flag = 1;
			}
	  }
	  else if(e.getSource() == buts[3]) {
	    buts[3].setIcon(icon2[3]);
	    if (flag == 0) {
				sendBuf_byte[6] = 100;
				sendBuf_byte[5] = (byte) (100 + 128);
    		sendByte(sendBuf_byte); 
    		flag = 1;
			}
	  }
	  else if(e.getSource() == buts[5]) {
	    buts[5].setIcon(icon2[5]);	    	    
	    if (flag == 0) {
				sendBuf_byte[6] = (byte) (100 + 128);
				sendBuf_byte[5] = 100;    		sendByte(sendBuf_byte); 
    		flag = 1;
			}
	  }
	  else if(e.getSource() == buts[6]) {
	    buts[6].setIcon(icon2[6]);
	    if (flag == 0) {
						sendBuf_byte[6] = (byte) (100 + 128);
						sendBuf_byte[5] = (byte) (70 + 128);
    		sendByte(sendBuf_byte); 
    		flag = 1;
			}
	  }
	  else if(e.getSource() == buts[7]) {
	    buts[7].setIcon(icon2[7]);
	    if (flag == 0) {
						sendBuf_byte[6] = (byte) (100 + 128);
						sendBuf_byte[5] = (byte) (100 + 128);
    		sendByte(sendBuf_byte); 
    		flag = 1;
			}
	  }
	  else if(e.getSource() == buts[8]) {
	    buts[8].setIcon(icon2[8]);
	    if (flag == 0) {
						sendBuf_byte[6] = (byte) (70 + 128);
						sendBuf_byte[5] = (byte) (100 + 128);
    		sendByte(sendBuf_byte); 
    		flag = 1;
			}
	  }	  	  	  	  	  	  
	}
//uplbt, upbt, uprbt, lbt, stopbt, rbt, downlbt, downbt, downrbt	    	
	public void mouseReleased(MouseEvent e)  {
	  if(e.getSource() == buts[0] 
	  || e.getSource() == buts[1]
	  || e.getSource() == buts[2]
	  || e.getSource() == buts[3]
	  || e.getSource() == buts[4]
	  || e.getSource() == buts[5]
	  || e.getSource() == buts[6]
	  || e.getSource() == buts[7]
	  || e.getSource() == buts[8]) {
 	    for(int i=0; i < icon2.length; i++)
 	      buts[i].setIcon(icon1[i]);
 	      
	    if (flag == 1) {
     		sendBuf_byte[6] = 0;
    		sendBuf_byte[5] = 0;
    		sendByte(sendBuf_byte); 
    		flag = 0;
			}
	  }	  
	  
	}
	
	public void mouseDragged(MouseEvent e) {
  
  }

	public void mouseMoved(MouseEvent e) {}
	
	
	
	public synchronized void onResume() {;
		if (btControl != null) {
			if (btControl.getState() == BTControl.STATE_NONE) {
				btControl.start();
			}
		}
	}	
	
	public void onDestroy() {
		if (btControl != null)
			btControl.stop();
	}
	
	public void onConnect() {
			if (btControl == null) {
			  try {
  				btControl = new BTControl(mHandle);
  				btControl.start();
				} catch(Exception e) {
				  e.printStackTrace();
				}
			}
	}		
		
		
	public void sendByte(byte[] Sendbuf) {
		// Check that we're actually connected before trying anything
		if (btControl != null) {
		if (btControl.getState() != BTControl.STATE_CONNECTED) {
			return;
		}

			btControl.Sendbyte(Sendbuf);
		}			
	}
	

	public double PSDADC(double adc) {
		double k, returnk;
		int k1;
		k = 5 * adc / 255;
		if (k >= 1.3) {
			k1 = (int) (-(k - 3.87) * 14 / 1.8);
		} else if (k >= 0.9 && k < 1.3) {
			k1 = (int) (-(k - 2.1) * 10 / 0.4);
		} else if (k >= 0.5 && k < 0.9) {
			k1 = (int) (-(k - 1.3) * 30 / 0.4);
		} else if (k >= 0.4 && k < 0.5) {
			k1 = (int) (-(k - 0.95) * 20 / 0.15);
		} else
			k1 = 80;

		returnk = (double) k1;

		return returnk;
	}    

	
  public static void main(String args[]) {
    KinectControl kc = new KinectControl();
    kc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    kc.pack();

    kc.setVisible(true);
  }	
  
  
  
///////////////////////////////////////////////////////////////////////////////

class JoystickPanel extends JPanel implements MouseListener, MouseMotionListener, Runnable {
   Image offScrImg;
   Graphics offScrGrp;   
   int nWidth, nHeight;
   int offWidth, offHeight;
   int imgWidth, imgHeight;
   Thread tThread;
   int x=0, y=0, w=150, h=150, w2=100, h2=100;   
   
		int dimX;
		int cX;
		int cY;
		
//   int XX=x+(w/2) - (w2/2);
//   int YY=y+(h/2) - (h2/2);
   int XX=0;
   int YY=0;   
   int flag=1;
   private int tcnt = 0;
   
  public JoystickPanel() {
		if(tThread == null) {
			tThread = new Thread(this);
			tThread.start( );
		}
		
		addMouseListener(this);
		addMouseMotionListener(this);
		
	}
	
	public void run() {
	}
	  
	public void paint(Graphics g){
		update(g);
	}

			public void update(Graphics g){ 	
			Dimension d = getSize();

			if(nWidth != d.width || nHeight != d.height) {
			  
    		int md = Math.min(d.width, d.height);

    		dimX = md;
    		cX = d.width/2 + 50;
    		cY = d.height/2;
			  
				nWidth = d.width;
				nHeight = d.height;
				x = cX - w/2;
				y = cY - h/2;
				if(XX == 0) XX = cX;
				if(YY == 0) YY = cY;
				setSize(nWidth, nHeight);
			}

			offWidth = nWidth;
			offHeight = nHeight;    

			if(offScrImg == null) {
				offScrImg = createImage (offWidth , offHeight);
				if(offScrImg == null) System.out.println("offScrImg is null");

				offScrGrp = offScrImg.getGraphics();		
			}

			offScrGrp.clearRect(0, 0, offWidth, offHeight);	    
			//offScrGrp.drawImage(offScrImg, 0, 0, this);
			
			

			offScrGrp.setColor(new Color(170, 170, 170)); // gray
			offScrGrp.fillOval(x, y, w, h);
			
			offScrGrp.setColor(new Color(50, 50, 50)); // gray
			offScrGrp.drawLine(cX-(w/2), cY, cX+(w/2), cY);
			offScrGrp.drawLine(cX, cY-(h/2), cX, cY+(h/2));

			if(XX-(w2/2) < x) XX = x+(w2/2);
			if(YY-(h2/2) < y) YY = y+(h2/2);

			if(XX+(w2/2) > (x+w)) XX = x+w-(w2/2);
			if(YY+(h2/2) > (y+h)) YY = y+h-(h2/2);

			offScrGrp.setColor(new Color(180, 0, 0));    // red
			offScrGrp.fillOval(XX-(w2/2), YY-(h2/2), w2, h2);

			//System.out.println("flag="+flag);
			int val=50;			
			if(flag==0) {
				int goX = XX, goY = YY;
        goX = goX - cX;
        goY = goY - cY;
				OnMoved(goX, goY);
			} else {
				offScrGrp.setColor(new Color(0,122,222));
				offScrGrp.drawString("X", 20, 50);     offScrGrp.drawString("released", 40, 50);
				offScrGrp.drawString("Y", 20, 80);     offScrGrp.drawString("released", 40, 80);      
     		sendBuf_byte[6] = 0;
    		sendBuf_byte[5] = 0;
    		sendByte(sendBuf_byte); 
				flag=0;
			}


			while(!g.drawImage(offScrImg, 0, 0, this));    

		}
	
	public void OnMoved(int pan, int tilt) {
    int Lwheel, Rwheel;
    
		tilt = tilt * -1;
		Lwheel = (int) (pan * Math.cos(315 * (Math.PI / 180)) - (tilt * Math
				.sin(315 * (Math.PI / 180))));
		Rwheel = (int) (pan * Math.sin(315 * (Math.PI / 180)) + (tilt * Math
				.cos(315 * (Math.PI / 180))));
				
    //System.out.println("Lwheel="+ Lwheel + " Rwheel="+ Rwheel );
    				
		if (Lwheel >= 7)
			Lwheel = 7;
		if (Lwheel <= -7)
			Lwheel = -7;
		if (Rwheel >= 7)
			Rwheel = 7;
		if (Rwheel <= -7)
			Rwheel = -7;


		//txtX.setText(Integer.toString(Rwheel));
		//txtY.setText(Integer.toString(Lwheel));
		if(offScrGrp != null) {
  		offScrGrp.setColor(Color.BLACK);
	  	offScrGrp.drawString("X", 20, 50);     offScrGrp.drawString(Integer.toString(Rwheel), 40, 50);
		  offScrGrp.drawString("Y", 20, 80);     offScrGrp.drawString(Integer.toString(Lwheel), 40, 80);		
      //offScrGrp.drawString("X", 20, 50);     offScrGrp.drawString(Integer.toString(Rwheel) + " / " + pan , 40, 50);
		  //offScrGrp.drawString("Y", 20, 80);     offScrGrp.drawString(Integer.toString(Lwheel) + " / " + tilt, 40, 80);				  
		}

		Lwheel = Lwheel * (100 / 7);
		Rwheel = Rwheel * (100 / 7);

		if (Lwheel < 0)
			Lwheel = 128 - Lwheel;
		if (Rwheel < 0)
			Rwheel = 128 - Rwheel;

		sendBuf_byte[6] = (byte) (Rwheel);
		sendBuf_byte[5] = (byte) (Lwheel);
		if (tcnt % 3 == 0) {
			sendByte(sendBuf_byte);
		}
		tcnt++;
	}		
  
	public void mouseClicked(MouseEvent e) {	  
	}
	public void mouseEntered(MouseEvent e)  {
 	}
	public void mouseExited(MouseEvent e)  {
	}
	public void mousePressed(MouseEvent e)  {	  
	}
	public void mouseReleased(MouseEvent e)  {	  	  
    XX=x+(w/2);
    YY=y+(h/2);   
    //offScrGrp.setColor(new Color(50, 50, 50));    
    //offScrGrp.fillOval(XX+(w/2)-(w2/2), YY+(h/2)-(h2/2), w2, h2);	  
    
    flag=1;
    
    
    repaint();
	}	
	
	public void mouseDragged(MouseEvent e) {
	  XX = e.getX(); 
	  YY = e.getY();  
	  
	  repaint();
	}
	public void mouseMoved(MouseEvent e) {}  
  
}

///////////////////////////////////////////////////////////////////////////////  

class TrackerPanel extends JPanel implements Runnable, GesturesWatcher
{
  private static final int MAX_DEPTH_SIZE = 10000;  

  private Color USER_COLORS[] = {
    Color.RED, Color.BLUE, Color.CYAN, Color.GREEN,
    Color.MAGENTA, Color.PINK, Color.YELLOW, Color.WHITE};
          /* colors used to draw each user's depth image, except the last
             (white) which is for the background */ 

  private byte[] imgbytes;
  private int imWidth, imHeight;
  private float histogram[];        // for the depth values
  private int maxDepth = 0;         // largest depth value


  private volatile boolean isRunning;
  
  // used for the average ms processing information
  private int imageCount = 0;
  private long totalTime = 0;
  private DecimalFormat df;
  private Font msgFont;

  // OpenNI
  private Context context;
  private DepthMetaData depthMD;

  private SceneMetaData sceneMD;
      /* used to create a labeled depth map, where each pixel holds a user ID
         (1, 2, etc.), or 0 to mean it is part of the background
      */

  private Skeletons skels;   // the users' skeletons


  public TrackerPanel()
  {
    setBackground(Color.WHITE);

    df = new DecimalFormat("0.#");  // 1 dp
    msgFont = new Font("SansSerif", Font.BOLD, 18);

    configOpenNI();

    histogram = new float[MAX_DEPTH_SIZE];

    imWidth = depthMD.getFullXRes();
    imHeight = depthMD.getFullYRes();
    System.out.println("Image dimensions (" + imWidth + ", " +
                                              imHeight + ")");
    // create empty image bytes array of correct size and type
    imgbytes = new byte[imWidth * imHeight * 3];

    new Thread(this).start();   // start updating the panel
  } // end of TrackerPanel()



  private void configOpenNI()
  /* create context, depth generator, depth metadata, 
     user generator, scene metadata, and skeletons
  */
  {
    try {
      context = new Context();
      
      // add the NITE Licence 
      License license = new License("PrimeSense", 
                        "0KOIk2JeIBYClPWVnMoRKn5cdY4=");   // vendor, key
      context.addLicense(license); 
      
      DepthGenerator depthGen = DepthGenerator.create(context);
      MapOutputMode mapMode = new MapOutputMode(640, 480, 30);   // xRes, yRes, FPS
      depthGen.setMapOutputMode(mapMode); 
      
      context.setGlobalMirror(true);         // set mirror mode 

      depthMD = depthGen.getMetaData();
           // use depth metadata to access depth info (avoids bug with DepthGenerator)

      UserGenerator userGen = UserGenerator.create(context);
      sceneMD = userGen.getUserPixels(0);
         // used to return a map containing user IDs (or 0) at each depth location

      skels = new Skeletons(userGen, depthGen, this);

      context.startGeneratingAll(); 
      System.out.println("Started context generating..."); 
    } 
    catch (Exception e) {
      System.out.println(e);
      System.exit(1);
    }
  }  // end of configOpenNI()



  public Dimension getPreferredSize()
  { return new Dimension(imWidth, imHeight); }


  public void closeDown()
  {  isRunning = false;  } 


  public void run()
  /* update and display the users-coloured depth image and skeletons
     whenever the context is updated.
  */
  {
    isRunning = true;
    while (isRunning) {
      try {
        context.waitAnyUpdateAll();
      }
      catch(StatusException e)
      {  System.out.println(e); 
         System.exit(1);
      }
	    long startTime = System.currentTimeMillis();
      updateUserDepths();
      skels.update();
      imageCount++;
      totalTime += (System.currentTimeMillis() - startTime);
      repaint();
    }
    // close down
    try {
      context.stopGeneratingAll();
    }
    catch (StatusException e) {}
    context.release();
    System.exit(0);
  }  // end of run()



  private void updateUserDepths()
  /* build a histogram of 8-bit depth values, and convert it to
     depth image bytes where each user is coloured differently */
  {
    ShortBuffer depthBuf = depthMD.getData().createShortBuffer();
    calcHistogram(depthBuf);
    depthBuf.rewind();

   // use user IDs to colour the depth map
    ShortBuffer usersBuf = sceneMD.getData().createShortBuffer();
      /* usersBuf is a labeled depth map, where each pixel holds an
         user ID (e.g. 1, 2, 3), or 0 to denote that the pixel is
         part of the background.  */

    while (depthBuf.remaining() > 0) {
      int pos = depthBuf.position();
      short depthVal = depthBuf.get();
      short userID = usersBuf.get();

      imgbytes[3*pos] = 0;     // default colour is black when there's no depth data
      imgbytes[3*pos + 1] = 0;
      imgbytes[3*pos + 2] = 0;

      if (depthVal != 0) {   // there is depth data
        // convert userID to index into USER_COLORS[]
        int colorIdx = userID % (USER_COLORS.length-1);   // skip last color

        if (userID == 0)    // not a user; actually the background
          colorIdx = USER_COLORS.length-1;   
                // use last index: the position of white in USER_COLORS[]

        // convert histogram value (0.0-1.0f) to a RGB color
        float histValue = histogram[depthVal];
        imgbytes[3*pos] = (byte) (histValue * USER_COLORS[colorIdx].getRed());
        imgbytes[3*pos + 1] = (byte) (histValue * USER_COLORS[colorIdx].getGreen());
        imgbytes[3*pos + 2] = (byte) (histValue * USER_COLORS[colorIdx].getBlue());
      }
    }
  }  // end of updateUserDepths()



  private void calcHistogram(ShortBuffer depthBuf)
  {
    // reset histogram
    for (int i = 0; i <= maxDepth; i++)
      histogram[i] = 0;

    // record number of different depths in histogram[]
    int numPoints = 0;
    maxDepth = 0;
    while (depthBuf.remaining() > 0) {
      short depthVal = depthBuf.get();
      if (depthVal > maxDepth)
        maxDepth = depthVal;
      if ((depthVal != 0)  && (depthVal < MAX_DEPTH_SIZE)){      // skip histogram[0]
        histogram[depthVal]++;
        numPoints++;
      }
    }
    // System.out.println("No. of numPoints: " + numPoints);
    // System.out.println("Maximum depth: " + maxDepth);

    // convert into a cummulative depth count (skipping histogram[0])
    for (int i = 1; i <= maxDepth; i++)
      histogram[i] += histogram[i-1];

    /* convert cummulative depth into the range 0.0 - 1.0f
       which will later be used to modify a color from USER_COLORS[] */
    if (numPoints > 0) {
      for (int i = 1; i <= maxDepth; i++)    // skipping histogram[0]
        histogram[i] = 1.0f - (histogram[i] / (float) numPoints);
    }
  }  // end of calcHistogram()



  // -------------------- drawing -------------------------

  public void paintComponent(Graphics g)
  // Draw the depth image with coloured users, skeletons, and statistics info
  { 
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;

    drawUserDepths(g2d);
    g2d.setFont(msgFont);    // for user status and stats
    skels.draw(g2d);
    writeStats(g2d);
  } // end of paintComponent()



  private void drawUserDepths(Graphics2D g2d)
  /* Create BufferedImage using the depth image bytes
     and a color model, then draw it */
  {
    // define an 8-bit RGB channel color model
    ColorModel colorModel = new ComponentColorModel(
                     ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8, 8},
                     false, false, ComponentColorModel.OPAQUE, DataBuffer.TYPE_BYTE);

    // fill the raster with the depth image bytes
    DataBufferByte dataBuffer = new DataBufferByte(imgbytes, imWidth*imHeight*3);

    WritableRaster raster = Raster.createInterleavedRaster(dataBuffer, imWidth,
                                 imHeight, imWidth*3, 3, new int[] { 0, 1, 2}, null);

    // combine color model and raster to create a BufferedImage
    BufferedImage image = new BufferedImage(colorModel, raster, false, null);

    g2d.drawImage(image, 0, 0, null);
  }  // end of drawUserDepths()



  private void writeStats(Graphics2D g2d)
  /* write statistics in bottom-left corner, or
     "Loading" at start time */
  {
	  g2d.setColor(Color.BLUE);
    int panelHeight = getHeight();
    if (imageCount > 0) {
      double avgGrabTime = (double) totalTime / imageCount;
	    g2d.drawString("Pic " + imageCount + "  " +
                   df.format(avgGrabTime) + " ms", 
                   5, panelHeight-10);  // bottom left
    }
    else  // no image yet
	    g2d.drawString("Loading...", 5, panelHeight-10);
  }  // end of writeStats()



  // ------------GesturesWatcher.pose() -----------------------------


  public void pose(int userID, GestureName gest, boolean isActivated)
  // called by the gesture detectors
  {
    
      
    if (isActivated) {
      System.out.println(gest + " " + userID + " on");
      //RH_UP, RH_FWD, RH_OUT, RH_IN, RH_DOWN, LH_UP
      if(gest == GestureName.RH_UP) {      // UP: FORWARD
  	    if (flag == 0) {
    		sendBuf_byte[6] = 100;
    		sendBuf_byte[5] = 100;
      		sendByte(sendBuf_byte); 
      		flag = 1;
				state = 5;      		
  			}    		
      } 
      else if(gest == GestureName.RH_FWD) { // UP: FORWARD
	    if (flag == 0) {
    		sendBuf_byte[6] = 100;
    		sendBuf_byte[5] = 100;
    		sendByte(sendBuf_byte); 
    		flag = 1;
				state = 5;    		
			}     
      }
      else if(gest == GestureName.RH_OUT) {  // TURN LEFT
	    if (flag == 0) {
				sendBuf_byte[6] = 100;
				sendBuf_byte[5] = (byte) (100 + 128);
    		sendByte(sendBuf_byte); 
    		flag = 1;
				state = 5;    		
			}    
      }
      else if(gest == GestureName.RH_IN) {   // TURN RIGHT
	    if (flag == 0) {
				sendBuf_byte[6] = (byte) (100 + 128);
				sendBuf_byte[5] = 100;    		sendByte(sendBuf_byte); 
    		flag = 1;
				state = 5;    		
			}      
      }
      else if(gest == GestureName.RH_DOWN) { // STOP
     		sendBuf_byte[6] = 0;
    		sendBuf_byte[5] = 0;
    		sendByte(sendBuf_byte);     
    		flag = 0;    
      }
      else if(gest == GestureName.LH_UP) { // STOP
     		sendBuf_byte[6] = 0;
    		sendBuf_byte[5] = 0;
    		sendByte(sendBuf_byte);         
    		flag = 0;

      }                 
    }
    else
      System.out.println("                        " + gest + " " + userID + " off");
  }  // end of pose()
  

} // end of TrackerPanel class

//////////////////////////////////////////////////////////////////////////////////////
  
} // class