package database;

import model.Person;
import model.Role;

import java.util.ArrayList;
import java.util.List;

public class Data {
    private List<Person> persons = new ArrayList<>();

    public Data() {
        // Add some sample users to the database
        persons.add(new Person("1", "Wim", "Wiltenburg","john@example.com", "1234567890", Role.SALESPERSON, "wim", "wim"));
        persons.add(new Person("2", "Jane", "Smith","jane@example.com", "9876543210", Role.Manager, "def", "def"));
        persons.add(new Person("3", "Alice", "Johnson","alice@example.com", "5555555555", Role.Manager, "lll", "lll"));
    }

    public Person findPersonByUsername(String username) {
        for (Person person : persons) {
            if (person.getUsername().equals(username)) {
                return person;
            }
        }
        return null;
    }
}
