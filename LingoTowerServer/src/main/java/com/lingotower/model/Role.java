package com.lingotower.model;

/**
 * Represents the different roles a user can have in the system.
 * 
 * - USER: Regular player with access to all game features.
 * - ADMIN: Manages content but has no control over users or system settings.
 * - SUPERADMIN: Full access, can manage users, admins, and system settings.
 */
public enum Role {
    USER,       // Player in the system
    ADMIN,      // Content manager (cannot manage users)
    SUPERADMIN; // Full control over the system
}




