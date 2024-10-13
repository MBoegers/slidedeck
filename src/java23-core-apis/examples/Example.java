import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

void main() {
    var merlin = Map.of(
        "name", "Merlin",
        "city", "Herne",
        "street", "Mont-Cenis-Str.",
        "house", "294");

    var falk = Map.of(
        "name", "Falk",
        "city", "Darmstadt",
        "street", "Donno",
        "house", "12");

    var engine = new Engine();
    var rules = List.<Engine.Rule>of(new PrintAttendeeInfos());

    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> engine.execute(rules, merlin));
            executor.submit(() -> engine.execute(rules, falk));    
        }        
    }

    System.out.println("Execution finished");
}