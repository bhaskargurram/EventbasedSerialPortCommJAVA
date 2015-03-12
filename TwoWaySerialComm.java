import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.util.LinkedList;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This version of the TwoWaySerialComm example makes use of the 
 * SerialPortEventListener to avoid polling.
 *
 */
public class TwoWaySerialComm
{
    public TwoWaySerialComm()
    {
        super();
    }
    
    void connect ( String portName ) throws Exception
    {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if ( portIdentifier.isCurrentlyOwned() )
        {
            System.out.println("Error: Port is currently in use");
        }
        else
        {
            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
            
            if ( commPort instanceof SerialPort )
            {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(57600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                
                InputStream in = serialPort.getInputStream();
                OutputStream out = serialPort.getOutputStream();
                               
                
                
                serialPort.addEventListener(new SerialReader(in,out));
                serialPort.notifyOnDataAvailable(true);
				sendData(true,false,out);

            }
            else
            {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }     
    }
    static void sendData(boolean firstsend,boolean secondsend,OutputStream out)
	{
	if(firstsend)
	{
	try{
				String send="0111EE\r\n";
				System.out.println("Sending data ="+send);
				out.write(send.getBytes());
	}
	catch(IOException e)
	{
	
	}
	}
	else if(secondsend)
	{
	try{
				HexRecord h = new HexRecord("01");
				AxisData x = new AxisData(1000, 1500, 0, AxisData.X_AXIS);
				AxisData y = new AxisData(1002, 1502, 0, AxisData.Y_AXIS);
				AxisData z = new AxisData(1004, 1504, 255, AxisData.Z_AXIS);
				DataSet d = new DataSet(x, y, z);
        
				LinkedList<DataSet> ll = new LinkedList<DataSet>();
				ll.add(d);
				h.createHexRecord(ll);
				String send=h.getHexRecord();
				System.out.println("Sending data ="+send);
				out.write(send.getBytes());
				}
	catch(IOException e)
	{
	
	}
	
	}
	
	}
    /**
     * Handles the input coming from the serial port. A new line character
     * is treated as the end of a block in this example. 
     */
    public static class SerialReader implements SerialPortEventListener 
    {
        private InputStream in;
		private OutputStream out;
        private byte[] buffer = new byte[1024];
        private boolean firstreply;
        private boolean secondreply;

        public SerialReader ( InputStream in,OutputStream out )
        {
            this.in = in;
            this.out = out;
			firstreply=true;
			secondreply=false;
			}
        
        public void serialEvent(SerialPortEvent arg0) {
            int data;
          
            try
            {
                int len = 0;
                while ( ( data = in.read()) > -1 )
                {

					buffer[len++] = (byte) data;
                    if ( data == '\n' ) {
                        break;
                    }
                }
				String reply=new String(buffer,0,len);
              System.out.println("received msg= "+reply);
				if(firstreply&&reply.equals("0111EE\r\n"))
				{
				firstreply=false;
				secondreply=true;
				TwoWaySerialComm.sendData(firstreply,secondreply,out);
				
				}
				else if(firstreply)
				{
				System.out.println("The two Strings doesn't match");
				System.exit(-1);
				
				}
				else if(secondreply)
				{
				System.exit(1);

		
				}
            }
            catch ( IOException e )
            {
                e.printStackTrace();
                System.exit(-1);
            }             
        }

    }

      

    
    public static void main ( String[] args )
    {
        try
        {
            (new TwoWaySerialComm()).connect("COM1");
        }
        catch ( Exception e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}