package com.speedyg.btw.team;

/**
 * This class created for Basic Team Wars plugin.
 *
 * @author Ozgen, Yusuf Serhat
 * @version 1.0.0-ALFA
 * @since 18.12.2019
 */

public class PermissionLoader {

    private String description;
    private String name;

    public PermissionLoader(String name, String description) {
        if (name != null)
            this.name = name;
        else
            this.name = "NULL";
        if (description != null)
            this.description = description;
        else
            this.description = "NULL";
    }

    public String getDescription() {
        return this.description;
    }

    public String getName() {
        return this.name;
    }
}
