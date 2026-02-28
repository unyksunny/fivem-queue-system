import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class GameServer {
    private static final int MAX_SLOT = 200;
    private final Set<Player> activePlayers = new HashSet<>();
    private final Queue<Player> waitingQueue = new ArrayDeque<>();

    private final ReentrantLock lock = new ReentrantLock(true); // fair lock
    private final Condition slotAvailable = lock.newCondition();

    public void join(Player p) throws InterruptedException {

        lock.lock();

        try {
            waitingQueue.add(p);
            System.out.println(p.id + " queued at " + waitingQueue.size());

            while (waitingQueue.peek() != p || activePlayers.size() >= MAX_SLOT) {
                slotAvailable.await();
            }

            waitingQueue.poll();
            activePlayers.add(p);
            System.out.println(p.id + " entered");

        } finally {
            lock.unlock();

        }
    }

    public void leave(Player p) {
        lock.lock();
        try {
            if (activePlayers.remove(p)) {
                System.out.println(p.id + " left");
                slotAvailable.signal(); // wake only ONE thread
            }
        } finally {
            lock.unlock();
        }
    }

    public int activeCount() {
        lock.lock();
        try {
            return activePlayers.size();
        } finally {
            lock.unlock();
        }
    }
}