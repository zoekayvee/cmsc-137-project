import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GameState {
  private Map players = new HashMap();

  public void addPlayer(String id, NetPlayer player) {
    players.put(id, player);
  }

  // Returns
  public Map getPlayers() {
    return players;
  }
}
