package packetserial;
import processing.serial.*;

public class PacketSerial
{

  public PacketSerial(int[] registar, Serial port)
  {
    _registar = registar;
    _myport = port;
  }
  
  public int getRegisterData(int add)
  {
    return _registar[add];
  }
  
  public void writeData(int data, int reg)
  {
      int[] dataBytes =
      {
          (data >> 24) & 0xFF,
          (data >> 16) & 0xFF,
          (data >>  8) & 0xFF,
          (data >>  0) & 0xFF
      };
  
      _myport.write(HEAD_BYTE);
      _myport.write(reg);
      int checksum = 0;
      for (int i = 0; i < 4; ++i)
      {
          if ((dataBytes[i] == ESCAPE_BYTE) || (dataBytes[i] == HEAD_BYTE))
          {
              _myport.write(ESCAPE_BYTE);
              checksum += ESCAPE_BYTE;
              _myport.write(dataBytes[i] ^ ESCAPE_MASK);
              checksum += dataBytes[i] ^ ESCAPE_MASK;
          }
          else
          {
              _myport.write(dataBytes[i]);
              checksum += dataBytes[i];
          }
      }
  
      _myport.write(checksum);
  }
  public void readData()
  {
      int[] bytes = {0,0,0,0};
      int checksum = 0;
      //delayMicroseconds(500);
      //delay(1);
      int data = _myport.read();
      //delayMicroseconds(500);
      //delay(1);
  
      if (data == HEAD_BYTE)
      {
          int reg = _myport.read();
          for (int i = 0; i < 4; ++i)
          {
              int d = _myport.read();
              //delayMicroseconds(500);
              //delay(1);
              if (d == ESCAPE_BYTE)
              {
                  int nextByte = _myport.read();
                  bytes[i] = nextByte ^ ESCAPE_MASK;
                  checksum += (d + nextByte);
              }
              else
              {
                  bytes[i] = d;
                  checksum += d;
              }
          }
          int checksum_recv = _myport.read();
          //delayMicroseconds(500);
          //delay(1);
          int DATA = 0x00;
          for(int i = 0; i < 4; i++)
          {
              DATA |= (((int)bytes[i]) << (24 - (i*8)));
          }
  
          if (checksum == checksum_recv)
          {
              _registar[reg] =  DATA;
          }
          else
          {
              // data error
          }
      }
  }
  
  private int[] _registar;
  private Serial _myport;
  
  private int HEAD_BYTE = 0x1D;
  private int ESCAPE_BYTE = 0x1E;
  private int ESCAPE_MASK = 0x1F;
};
