import org.flywaydb.core.Flyway;

public class DatabaseInitServiceV {
    private static final String URL = "jdbc:h2:./test";

    public static void main(String[] args) {

        Flyway flyway = Flyway
                .configure()
                .dataSource(URL, null, null)
                .load();
        flyway.migrate();
    }
}