import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class GameServer {
    private static final int MAX_SLOT = 200;
    private final Set<Player> activePlayers = new HashSet<>();
    private final Queue<Player> waitingQueue = new ArrayDeque<>();

    private final Object lock = new Object();

    public void join(Player p) throws InterruptedException {

        synchronized (lock) {

            waitingQueue.add(p);
            System.out.println(p.id + " queued at " + waitingQueue.size());

            while (true) {

                boolean canEnter = waitingQueue.peek() == p &&
                        activePlayers.size() < MAX_SLOT;

                if (canEnter) {
                    waitingQueue.poll();
                    activePlayers.add(p);
                    System.out.println(p.id + " entered");
                    return;
                }

                lock.wait();
            }
        }
    }

    public void leave(Player p){
        synchronized (lock) {
            if(activePlayers.remove(p)){
                System.out.println(p.id + " left");
                lock.notifyAll();
            }
        }
    }

    public int activeCount() {
        synchronized (lock) {
            return activePlayers.size();
        }
    }
}