package model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Client {
    private long id;
    private String name;

    public Client(long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "id =" + id +
                ", name = " + name + "\n";
    }
}
