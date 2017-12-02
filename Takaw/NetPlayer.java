import java.net.InetAddress;
import java.util.UUID;

public class NetPlayer {
  private InetAddress address;
  private int port;
  private String name;

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
    return port
  }
}
