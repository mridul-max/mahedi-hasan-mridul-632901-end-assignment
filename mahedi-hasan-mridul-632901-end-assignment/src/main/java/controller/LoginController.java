package controller;

import database.Data;
import model.Person;

public class LoginController {

    private Data data;

    public LoginController(Data data) {
        this.data = data;
    }

    public Person login(String username, String password) {
        // Check if the user exists in the database.
        Person person = data.findPersonByUsername(username);
        // If the user does not exist, return null.
        if (person == null) {
            return null;
        }
        // Verify the password.
        if (!person.getPassword().equals(password)) {
            return null;
        }
        // Return the Person object if the login is successful.
        return person;
    }
}

