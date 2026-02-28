import java.util.*;

public class Main {

    public static void main(String[] args) {

        GameServer server = new GameServer();
        Random random = new Random();

        int totalSimulatedUsers = 300;

        for (int i = 1; i <= totalSimulatedUsers; i++) {

            final Player p = new Player("P" + i);

            new Thread(() -> {
                while (true) {
                    try {

                        server.join(p);

                        // playing time
                        Thread.sleep(10000 + random.nextInt(4000));

                        server.leave(p);

                        // thinking time (outside system)
                        Thread.sleep(1000 + random.nextInt(3000));

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