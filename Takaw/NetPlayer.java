/*
* Player data and information
*/

import java.net.InetAddress;
import java.util.UUID;

public class NetPlayer {
  private InetAddress address;
  private int port;
  private String name;
  private String id;
  private int x,y;

  public NetPlayer(String id, String name, InetAddress address, int port) {
    this.address = address;
    this.port = port;
    this.name = name;
    this.id = id;
  }

  public InetAddress getAddress() {
    return address;
  }

  public int getPort() {
    return port;
  }

  public String getName() {
    return name;
  }

  public String getID() {
    return id;
  }

  public void setX(int x){
		this.x=x;
  }

  public int getX(){
		return x;
	}

  public int getY(){
		return y;
	}

    public void setY(int y){
		this.y=y;
	}

	public String toStringAdd(){
      String retval="";
      retval+="ADDPLAYER ";
      retval+=id+" ";
      retval+=name+" ";
      retval+=x+" ";
      retval+=y;
      return retval;
    }

    public String toString(){
        String retval="";
        retval+="PLAYER ";
        retval+=id+" ";
        retval+=x+" ";
        retval+=y;
        return retval;
    }
}
