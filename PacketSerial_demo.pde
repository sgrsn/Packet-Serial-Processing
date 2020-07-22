import processing.serial.*;
import packetserial.*;

Serial myPort = new Serial(this, Serial.list()[0], 9600);

int[] register = new int[32];
PacketSerial myserial = new PacketSerial(register, myPort);

void setup()
{
}

void draw()
{   
  if ( myPort.available() > 0 ) 
  {
    myserial.readData();
  }
  println(myserial.getRegisterData(0)); 
}

/*
void serialEvent(Serial p)
{
}
*/
