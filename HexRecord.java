

import java.util.LinkedList;

public class HexRecord {
    private String slaveAddress;
    private String record;
    private String checksum;
    private int byteCount;
    
    public HexRecord(String slaveAddress){
        this.slaveAddress = slaveAddress;
        this.record = ":";
        this.byteCount = 0;
        this.checksum = "";
    }
    
    String getHexRecord(){
        return this.record;
    }
    //size unbounded should be limited to 2^16 - 1
    void createHexRecord(LinkedList<DataSet> ll){
        this.record += this.slaveAddress;//slave address appended
        this.byteCount = ll.size()*15;
        this.record += Integer.toHexString( 0x10000 | this.byteCount).substring(1).toUpperCase();//length of data appended
        String tempstr = "";
        for (int i =0;i< ll.size();i++){
            tempstr += ll.get(i).generateHexData();
        }
        this.record += tempstr;//data appended
        this.checksum = generateLrc(tempstr);
        this.record += this.checksum; //lrc appended
        this.record += "\r\n";
    }
    
    String generateLrc(String s){
        int sum = 0;
        for (int i = 0; i < s.length()/2; i++) {
	int index = i * 2;
	int v = Integer.parseInt(s.substring(index, index + 2), 16);
	sum += v;
	}
        int slave_addrs_int=Integer.parseInt(this.slaveAddress);
        sum=sum+slave_addrs_int+this.byteCount;
        sum = ~sum;
        sum++;
        String lrc=Integer.toHexString( 0x100 | sum).substring(1).toUpperCase();
	String lrcappend=lrc.substring(lrc.length()-2);
        return lrcappend;
    }
    
    /*
    public static void main(String args[]){
        HexRecord h = new HexRecord("01");
         AxisData x = new AxisData(1000, 1500, 0, AxisData.X_AXIS);
        AxisData y = new AxisData(1002, 1502, 0, AxisData.Y_AXIS);
        AxisData z = new AxisData(1004, 1504, 255, AxisData.Z_AXIS);
        DataSet d = new DataSet(x, y, z);
        
        LinkedList<DataSet> ll = new LinkedList<DataSet>();
        ll.add(d);
        h.createHexRecord(ll);
        System.out.println(h.getHexRecord());
        
       //testing 
       String s1 = ":01000F03E805DC0003EA05DE0003EC05E0FF81\r\n";
        System.out.println(s1);
        if(s1.equals(h.getHexRecord())){
            System.out.println("True");
        }
        else{
            System.out.println("False");
        }
       
    } */
    
}
class AxisData{
    int distance, speed, direction;
    int axis;
    public static final int X_AXIS = 0;
    public static final int Y_AXIS = 1;
    public static final int Z_AXIS = 2;
    
    AxisData(int distance, int speed, int direction, int axis){
        this.distance = distance;
        this.speed = speed;
        this.direction = direction;
        this.axis = axis;
    }
}
class DataSet{
    AxisData x,y,z;
    DataSet(AxisData x, AxisData y, AxisData z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    String generateHexData(){
	String disx =Integer.toHexString( 0x10000 | this.x.distance).substring(1).toUpperCase();
	String spx = Integer.toHexString( 0x10000 | this.x.speed).substring(1).toUpperCase();
	String dirx =Integer.toHexString( 0x100 | this.x.direction).substring(1).toUpperCase();
		
	String disy =Integer.toHexString( 0x10000 | this.y.distance).substring(1).toUpperCase();
	String spy = Integer.toHexString( 0x10000 | this.y.speed).substring(1).toUpperCase();
	String diry =Integer.toHexString( 0x100 | this.y.direction).substring(1).toUpperCase();
		
	String disz =Integer.toHexString( 0x10000 | this.z.distance).substring(1).toUpperCase();
	String spz = Integer.toHexString( 0x10000 | this.z.speed).substring(1).toUpperCase();
	String dirz =Integer.toHexString( 0x100 | this.z.direction).substring(1).toUpperCase();
		
	return disx+spx+dirx+disy+spy+diry+disz+spz+dirz;
    }
}