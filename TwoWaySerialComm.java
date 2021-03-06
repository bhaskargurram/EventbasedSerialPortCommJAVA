import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.util.LinkedList;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


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
                               
                
                
                serialPort.addEventListener(new SerialReader(in));
                serialPort.notifyOnDataAvailable(true);


            }
            else
            {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }     
    }
//Call this function whenever you need to send data on the serial port
    static void sendData(OutputStream out,String msg)
	{
	
	
	try{
			
				System.out.println("Sending data ="+msg);
				out.write(msg.getBytes());
				}
	catch(IOException e)
	{
	
	}
	
	
	
	}
    /**
     * Handles the input coming from the serial port. A new line character
     * is treated as the end of a block in this example. 
     */
    public static class SerialReader implements SerialPortEventListener 
    {
        private InputStream in;
		
        private byte[] buffer = new byte[1024];


        public SerialReader ( InputStream in )
        {
            this.in = in;
   

			}
        
        public void serialEvent(SerialPortEvent arg0) {
            int data;
          
            try
            {
                int len = 0;
                while ( ( data = in.read()) > -1 )//retrieves ascii data to be taken byte by byte
                {

		buffer[len++] = (byte) data;
                    if ( data == '\n' ) {
                        break;
                    }
                }
              String reply=new String(buffer,0,len);
              System.out.println("received msg= "+reply);
				
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
            
            e.printStackTrace();
        }
    }


}
