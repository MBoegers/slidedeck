import java.time.Duration;
import java.util.List;
import java.util.Map;

/// This engine uses [JEP 481](https://openjdk.org/jeps/481) {@link ScopedValue} technique to forward execution context thread save.
class Engine {
    public static abstract class Rule {
        /// Models the Adress of an attendee.
        /// The `record` implementation ensures that the data is immutable.
        record Address(String city, String street, int house) {
        }

        /// Read the address from the **context** as via the scoped value API and generates the `Address` PoJo
        protected Address readAddress() throws InterruptedException {
            String city = CONTEXT.get().get("city");
            String street = CONTEXT.get().get("street");
            int house = Integer.parseInt(CONTEXT.get().get("house"));
            Thread.sleep(Duration.ofSeconds(1)); // simulate a time consuming operation
            return new Address(city, street, house);
        }

        protected String readName() {
            return CONTEXT.get().getOrDefault("name", "UNKNOWN");
        }

        /// Method to implement by the business rules.
        /// The implementations can Thread-save leverage the `Rule.read*` methods.
        abstract void fire();
    }

    private final static ScopedValue<Map<String, String>> CONTEXT = ScopedValue.newInstance();
    private final static ScopedValue<SSLContext> CONTEXT = ScopedValue.newInstance();

    void execute(List<Rule> rules, Map<String, String> data) {
        ScopedValue.Carrier executionScope = ScopedValue
                .where(CONTEXT, data)
                .where(SSL_CTX, SSLContext.getDefault());
        for (var r : rules) {
            executionScope.run(r::fire);
        }
    }
}
