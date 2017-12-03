import java.net.InetAddress;
import java.util.UUID;

public class NetPlayer {
  private InetAddress address;
  private int port;
  private String name;
  private String id;
  private int x,y;

  public NetPlayer(String name, InetAddress address, int port) {
    this.address = address;
    this.port = port;
    this.name = name;
    this.id = UUID.randomUUID().toString();
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
}
