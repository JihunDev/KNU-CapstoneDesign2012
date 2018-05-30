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


 
/**
* A simple SPP client that connects with an SPP server
*/

 // UUID: 00019511CB1A CNKRT0254
 // UUID: 18E2C26E64A7 SHV-E160K 
 
public class BTControl {

	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;
  	
	// Constants that indicate the current connection state
	public static final int STATE_NONE = 0; // we're doing nothing
	public static final int STATE_LISTEN = 1; // now listening for incoming
	// connections
	public static final int STATE_CONNECTING = 2; // now initiating an outgoing
	// connection
	public static final int STATE_CONNECTED = 3; // now connected to a remote  	
	
	private StreamConnection streamConnection;
 	private Handle mHandle;	
  private int mState;
  private byte[] tx_d = new byte[1024];   
	//private byte[] sendBuf_byte = new byte[22];
	//private byte[] readBuf_byte = new byte[31];
//  private byte[] readBuf = new byte[31];	
  int cnt=0;


  private static String connectionURL = "btspp://00019511CB1E:1;authenticate=false;encrypt=false;master=false";
    

  public BTControl(Handle handle) throws Exception {
		mHandle = handle;
    //start();
  }	
  
  public void start() {    
    
      //display local device address and name
      try {
      LocalDevice localDevice = LocalDevice.getLocalDevice();
      System.out.println("Address: "+localDevice.getBluetoothAddress());
      System.out.println("Name: "+localDevice.getFriendlyName()); 
      } catch(BluetoothStateException e) {
        e.printStackTrace();
      }
          
		// Cancel any thread attempting to make a connection
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}
	      
		setState(STATE_LISTEN);
		
		// Finding a Device to Connect
			      
    connect();
  }
    
	public synchronized void connect() {
	  System.out.println("connect()");

		// Cancel any thread attempting to make a connection
		if (mState == STATE_CONNECTING) {
			if (mConnectThread != null) {
				mConnectThread.cancel();
				mConnectThread = null;
			}
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// Start the thread to connect with the given device
		mConnectThread = new ConnectThread(connectionURL);
		mConnectThread.start();

		setState(STATE_CONNECTING);		

	}
	 
	/**
	 * Indicate that the connection was lost and notify the UI Activity.
	 */
	private void connectionLost() {
		setState(STATE_LISTEN);

		System.exit(0);
	}
		 
	/**
	 * Start the ConnectedThread to begin managing a Bluetooth connection
	 * 
	 * @param socket
	 *            The BluetoothSocket on which the connection was made
	 * @param device
	 *            The BluetoothDevice that has been connected
	 */
	public synchronized void connected(StreamConnection streamConnection) {
	  System.out.println("connected()");

		// Cancel the thread that completed the connection
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// Start the thread to manage the connection and perform transmissions
		mConnectedThread = new ConnectedThread(streamConnection);
		mConnectedThread.start();

		setState(STATE_CONNECTED);
	}

	/**
	 * Stop all threads
	 */
	public synchronized void stop() {
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
		setState(STATE_NONE);
	}

	public void Sendbyte(byte[] Sendbuf) {
		// Create temporary object
		ConnectedThread r;
		// Synchronize a copy of the ConnectedThread
		synchronized (this) {
			if (mState != STATE_CONNECTED)
				return;
			r = mConnectedThread;
		}
		// Perform the write unsynchronized
		r.Sendbyte(Sendbuf);
	}
		 
  
	/**
	 * Set the current state of the chat connection
	 * 
	 * @param state
	 *            An integer defining the current connection state
	 */
	private synchronized void setState(int state) {
		mState = state;

		// Give the new state to the Handle so the UI Activity can update
		//mHandler.obtainMessage(CRX10App_galaxySActivity.MESSAGE_STATE_CHANGE,	state, -1).sendToTarget();
	}

	/**
	 * Return the current connection state.
	 */
	public synchronized int getState() {
		return mState;
	}
    
       

	
	
  public static void main(String[] args) throws IOException {
    try {
        BTControl client=new BTControl(new Handle());
    } catch(Exception e) {
      e.printStackTrace();      
    }
  }
  	
   
	/**
	 * This thread runs while attempting to make an outgoing connection with a
	 * device. It runs straight through; the connection either succeeds or
	 * fails.
	 */
	private class ConnectThread extends Thread {
		private final String mmConnectionURL;

		public ConnectThread(String connectionURL) {
			mmConnectionURL = connectionURL;
		}

		@Override
		public void run() {
			setName("ConnectThread");

			// Make a connection to the BluetoothSocket
			try {
				streamConnection =(StreamConnection)Connector.open(mmConnectionURL);
			} catch (IOException e) {
				System.out.println("connectionFailed");
				// Start the service over to restart listening mode
				this.start();
				return;
			}

			// Reset the ConnectThread because we're done
			synchronized (this) {
				mConnectThread = null;
			}

			// Start the connected thread
			connected(streamConnection);
		}

		public void cancel() {
			try {
				streamConnection.close();
			} catch (IOException e) {
				System.out.println("close() of connect socket failed");
			}
		}
	}
	

  	
	/**
	 * This thread runs during a connection with a remote device. It handles all
	 * incoming and outgoing transmissions.
	 */
	private class ConnectedThread extends Thread {
		private final StreamConnection mmStreamConnection;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;    

		public ConnectedThread(StreamConnection streamConnection) {
			mmStreamConnection = streamConnection;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the BluetoothSocket input and output streams
			try {
				tmpIn = mmStreamConnection.openInputStream();
				tmpOut = mmStreamConnection.openOutputStream();
								
			} catch (IOException e) {
				System.out.println("temp sockets not created");
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		@Override
		public void run() {
			byte[] buffer = new byte[32];
			int bytes;

			// Keep listening to the InputStream while connected
			while (true) {
			  
				try {
					// Read from the InputStream
					bytes = mmInStream.read(buffer);
					/*
					if (cnt % 10 == 0) {
  					System.out.print(buffer[0] + " " + buffer[31] + " " + buffer[1] + " ");
      			for(int i=0; i < 7; i++) {
        			System.out.print(" " +"IR[" + (i+1)+ "]=" + buffer[8+i] + " ");
  	    		}
  			    System.out.println();  					
					}
					*/
					mHandle.sendMsg(buffer, mState);
					//readProc(bytes, buffer);
					cnt++;
					if(cnt >= Integer.MAX_VALUE) cnt=0;
				} catch (IOException e) {
					System.out.println("disconnected");
					connectionLost();
					System.exit(0);
					break;
				}
			}
		}

		private void check_sum_tx_calcuration() {
			int tx_check_sum;
			tx_check_sum = tx_d[0];
			tx_check_sum = tx_check_sum + tx_d[1];
			tx_check_sum = tx_check_sum + tx_d[3];
			tx_check_sum = tx_check_sum + tx_d[4];
			tx_check_sum = tx_check_sum + tx_d[5];
			tx_check_sum = tx_check_sum + tx_d[6];
			tx_check_sum = tx_check_sum + tx_d[7];
			tx_check_sum = tx_check_sum + tx_d[8];
			tx_check_sum = tx_check_sum + tx_d[9];
			tx_check_sum = tx_check_sum + tx_d[10];
			tx_check_sum = tx_check_sum + tx_d[11];
			tx_check_sum = tx_check_sum + tx_d[12];
			tx_check_sum = tx_check_sum + tx_d[13];
			tx_check_sum = tx_check_sum + tx_d[14];
			tx_check_sum = tx_check_sum + tx_d[15];
			tx_check_sum = tx_check_sum + tx_d[16];
			tx_check_sum = tx_check_sum + tx_d[17];
			tx_check_sum = tx_check_sum + tx_d[18];
			tx_check_sum = tx_check_sum + tx_d[19];
			tx_check_sum = tx_check_sum + tx_d[20];
			tx_check_sum = tx_check_sum + tx_d[21];

			tx_check_sum = tx_check_sum % 256;
			tx_d[2] = (byte) tx_check_sum;
		}

		public void Sendbyte(byte[] Sendbuf) {
			try {
				tx_d[0] = 2;
				tx_d[1] = 22;
				tx_d[3] = Sendbuf[3];
				tx_d[4] = 0;
				tx_d[5] = Sendbuf[5];
				tx_d[6] = Sendbuf[6];
				tx_d[7] = Sendbuf[7];
				tx_d[8] = Sendbuf[8];
				tx_d[9] = Sendbuf[9];
				tx_d[10] = Sendbuf[10];
				tx_d[11] = Sendbuf[11];
				tx_d[12] = Sendbuf[12];
				tx_d[13] = Sendbuf[13];
				tx_d[14] = Sendbuf[14];
				tx_d[15] = Sendbuf[15];
				tx_d[16] = Sendbuf[16];
				tx_d[17] = 0;
				tx_d[18] = 0;
				tx_d[19] = 0;
				tx_d[20] = 0;
				tx_d[21] = 3;

				check_sum_tx_calcuration();
				mmOutStream.write(tx_d);
			} catch (IOException e) {
				System.out.println("Exception during write");
			}
		}

		public void cancel() {
			try {
				mmStreamConnection.close();
			} catch (IOException e) {
				System.out.println("close() of connect socket failed");
			}
		}
	} // ConnectedThread
	
	
   
}
