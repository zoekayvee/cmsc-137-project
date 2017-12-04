import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GameState {
  private Map players = new HashMap();
  private Map food = new HashMap();

  public GameState() {

  }

  public void addPlayer(String id, NetPlayer player) {
    players.put(id, player);
  }

  public void generateFood(String id, NetFood orb) {food.put(id, orb);}

  public String playerData(){
      String retval="";
      for(Iterator ite=players.keySet().iterator();ite.hasNext();){
          String id=(String)ite.next();
          NetPlayer player=(NetPlayer)players.get(id);
          retval+=player.toStringAdd()+":";
      }
      System.out.println(retval);
      return retval;
  }

  // Returns player hashmap
  public Map getPlayers() {
    return players;
  }

}
