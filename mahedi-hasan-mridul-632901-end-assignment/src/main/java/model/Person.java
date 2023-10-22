package model;
public class Person {

    private String id;
    private String name;
    private String emailAddress;
    private String phoneNumber;
    private Role role;
    private String username;
    private String password;

    public Person(String id, String name, String emailAddress, String phoneNumber, Role role, String username, String password) {
        this.id = id;
        this.name = name;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.username = username;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Role getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
