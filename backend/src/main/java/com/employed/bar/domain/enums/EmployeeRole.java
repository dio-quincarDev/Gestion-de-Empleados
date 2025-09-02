package com.employed.bar.domain.enums;

public enum EmployeeRole {
    SECURITY("Seguridad"),
    WAITER("Salonero/a"),
    CASHIER("Cajero/a"),
    BARTENDER("Bartender"),
    CHEF("Cocinero/a Principal"),
    CHEF_ASSISTANT("Ayudante de Cocina"),
    STOCKER("Bodeguero/a"),
    MAINTENANCE("Mantenimiento"),
    MANAGER("Gerente"),
    ADMIN("Administrador/a"),
    HOST("Anfitrión/a"),
    DJ("DJ");

    private final String displayName;
    EmployeeRole(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName(){
        return displayName;
    }
}
