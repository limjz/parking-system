package controllers;

import utils.Config;

public class LoginController {
    
    public boolean authenticateAdmin(String user, String pass) {
        // In a real app, you would check a database here
        return Config.adminUser.equals(user) && Config.adminPass.equals(pass);
    }
}