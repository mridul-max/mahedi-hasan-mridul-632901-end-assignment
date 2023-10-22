package controller;

import database.Data;
import model.Person;

public class LoginController {

    private Data data;

    public LoginController(Data data) {
        this.data = data;
    }

    public Boolean login(String username, String password) {
        // Check if the user exists in the database.
        Person person = data.findPersonByUsername(username);
        // If the user does not exist, return an error.
        if (person == null) {
            return false;
        }

        // Verify the password.
        if (!person.getPassword().equals(password)) {
            return false;
        }
        return true;
    }
}

