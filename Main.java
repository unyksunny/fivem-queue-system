import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

    public static void main(String[] args) {

        GameServer server = new GameServer();
        Random random = new Random();

        int totalSimulatedUsers = 140;

        for (int i = 1; i <= totalSimulatedUsers; i++) {

            final Player p = new Player("P" + i);

            new Thread(() -> {
                while (true) {
                    try {
                        server.join(p);
                        // random stay duration (5s to 40s)
                        int stay = 5 + random.nextInt(35);

                        scheduler.schedule(() -> {
                            server.leave(p);
                        }, stay, TimeUnit.SECONDS);

                        // player thinks before rejoining (independent of leave)
                        Thread.sleep(3000 + random.nextInt(5000));

                    } catch (InterruptedException ignored) {
                    }
                }
            }).start();
        }

        // Monitor
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    System.out.println("ACTIVE = " + server.activeCount());
                } catch (InterruptedException ignored) {
                }
            }
        }).start();
    }
}