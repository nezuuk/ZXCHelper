package ru.emrass.zxchelper.hwidcontrol;

public enum Role {
    OWNER("владелец",2),
    ADMIN("админ",1),
    USER("юзер",0);

    private final String name;
    private final int level;
    Role(String name, int level){
        this.name = name;
        this.level = level;
    }

    public boolean hasPermission(Role requiredRole) {
        return this.level >= requiredRole.level;
    }


    public String getName() {
        return name;
    }
}