package controllers;

public class LoginController {
    private final String adminUser = "admin";
    private final String adminPass = "1234";

    public boolean authenticateAdmin(String user, String pass) {
        return adminUser.equals(user) && adminPass.equals(pass);
    }
}
