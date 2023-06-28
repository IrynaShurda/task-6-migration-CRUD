import model.Client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ClientService {
    private final PreparedStatement createSt;
    private final PreparedStatement getByIdSt;
    private final PreparedStatement getIdByNameSt;
    private final PreparedStatement setNameSt;
    private final PreparedStatement ifIdExistSt;
    private final PreparedStatement deleteByIdSt;
    private final PreparedStatement listAllSt;


    public static void main(String[] args) throws Exception {
        Connection connection = DriverManager.getConnection("jdbc:h2:./test");

        ClientService clientService = new ClientService(connection);
        long newClientId = clientService.create("New Option");
        System.out.println(" ID щойно створеного клієнта = " + newClientId);

        String idByName = clientService.getById(2);
        System.out.println("За вашим id знайдено клієнта = " + idByName);

        clientService.setName(3, "PUMA");
        System.out.println("Імя клієнта змінено на " + clientService.getById(3));

        System.out.println("Клієнта" +  clientService.getById(8) +" буде вилучено");
        clientService.deleteById(8);

        List<Client> clients = clientService.listAll();
        System.out.println("Перелік всіх клієнтів " + clients);

        connection.close();
    }

    public ClientService(Connection connection) throws SQLException {
        createSt = connection.prepareStatement(
                "INSERT INTO client (NAME) VALUES (?)"
        );
        getIdByNameSt = connection.prepareStatement(
                "SELECT id from CLIENT WHERE NAME = ?"
        );
        getByIdSt = connection.prepareStatement(
                "SELECT * FROM client WHERE id = ?"
        );
        setNameSt = connection.prepareStatement(
                "UPDATE client SET name = ? WHERE ID = ?"
        );
        ifIdExistSt = connection.prepareStatement(
                "SELECT COUNT(*) FROM client WHERE ID = ?"
        );
        deleteByIdSt = connection.prepareStatement(
                "DELETE FROM client WHERE ID = ?"
        );
        listAllSt = connection.prepareStatement(
                "SELECT * FROM client"
        );
    }

    long create(String name) throws Exception {
        validateName(name);

        createSt.setString(1, name);
        createSt.executeUpdate();

        long id;
        getIdByNameSt.setString(1, name);
        try (ResultSet rs = getIdByNameSt.executeQuery()) {
            rs.next();
            id = rs.getLong("id");
        }
        return id;
    }

    String getById(long id) throws Exception {
        validateId(id);
        String name;
        getByIdSt.setLong(1, id);
        try (ResultSet rs = getByIdSt.executeQuery()) {
            rs.next();
            name = rs.getString("name");
        }
        return name;
    }

    private boolean checkIFIdExists(long id) throws SQLException {
        ifIdExistSt.setLong(1, id);
        ResultSet resultSet = ifIdExistSt.executeQuery();
        resultSet.next();
        int count = resultSet.getInt(1);
        return count > 0;
    }

    void setName(long id, String name) throws Exception {
        validateName(name);
        validateId(id);
        setNameSt.setString(1, name);
        setNameSt.setLong(2, id);
        setNameSt.executeUpdate();
    }

    void deleteById(long id) throws Exception {
        validateId(id);
        deleteByIdSt.setLong(1, id);
        deleteByIdSt.executeUpdate();
    }

    List<Client> listAll() throws SQLException {
        List<Client> clients = new ArrayList<>();
        ResultSet resultSet = listAllSt.executeQuery();
        while (resultSet.next()) {
            long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            Client client = new Client(id, name);
            clients.add(client);
        }
        return clients;
    }

    private void validateId(long id) throws Exception {
        if (id <= 0) {
            throw new Exception("Невірне значення id. Поле не може бути порожнім або від'ємним");
        }
        if (!checkIFIdExists(id)) {
            throw new Exception("ID не існує. Перевірте коректність вводу");
        }
    }

    private static void validateName(String name) throws Exception {
        if (name == null) {
            throw new Exception("Невірне значення name. Поле не може бути порожнім");
        }
        if (name.length() <= 2 || name.length() > 1000) {
            throw new Exception("Невірне значення name. Поле повинно мати мінімум  2 і не більше 1000 символів");
        }
    }
}