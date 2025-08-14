import java.util.*;
import java.util.concurrent.*;
import java.text.SimpleDateFormat;

class Incident {
    private String type;
    private String location;
    private String timestamp;

    public Incident(String type, String location) {
        this.type = type;
        this.location = location;
        this.timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    public String getDetails() {
        return "[" + timestamp + "] " + type + " reported at " + location;
    }
}

class IncidentDashboard {
    private BlockingQueue<Incident> incidentQueue = new LinkedBlockingQueue<>();

    public void addIncident(Incident incident) {
        try {
            incidentQueue.put(incident);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void displayIncidents() {
        while (true) {
            try {
                Incident incident = incidentQueue.take();
                System.out.println(incident.getDetails());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        IncidentDashboard dashboard = new IncidentDashboard();

        // Thread to simulate incoming incidents
        Thread producer = new Thread(() -> {
            String[] types = {"Fire", "Medical", "Police"};
            String[] locations = {"Downtown", "Uptown", "Suburbs"};
            Random rand = new Random();

            for (int i = 0; i < 10; i++) {
                String type = types[rand.nextInt(types.length)];
                String location = locations[rand.nextInt(locations.length)];
                dashboard.addIncident(new Incident(type, location));

                try {
                    Thread.sleep(1000); // Simulate delay between incidents
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        // Thread to display incidents in real-time
        Thread consumer = new Thread(dashboard::displayIncidents);

        producer.start();
        consumer.start();
    }
}