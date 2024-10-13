import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.function.Supplier;

/// This Rule leverages the [JEP 480](https://openjdk.org/jeps/480) StructuredConcurrency API to obtain data for the logic
class PrintAttendeeInfos extends Engine.Rule{

    @Override
    void fire() {

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Supplier<String> name = scope.fork(() -> {
                System.out.println("\tRead Name");
                var tmp = readName();
                System.out.println("\tGot Name");
                return tmp;
            });
            Supplier<Address> adress   = scope.fork(() ->{
                System.out.println("\tRead Address");
                var tmp = readAddress();
                System.out.println("\tGot Address");
                return tmp;
            });

            scope.join().throwIfFailed();  // Wait for both forks

            var addr = adress.get();
            System.out.printf("%s's home is %s %d, %s%n", name.get(), addr.street(), addr.house(), addr.city());
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}