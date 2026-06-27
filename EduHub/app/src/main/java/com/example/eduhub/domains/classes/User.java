package com.example.eduhub.domains.classes;

import android.os.Build;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class User {
    protected UUID id;
    protected String email;
    protected String passwordHash;
    protected String firstName;
    protected String lastName;
    protected String patronymic;
    protected String phoneNumber;
    protected LocalDateTime createdAt;
    protected LocalDateTime lastLoginAt;
    protected boolean isActive;
    protected String avatarUrl;

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPatronymic() { return patronymic; }
    public void setPatronymic(String patronymic) { this.patronymic = patronymic; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public User() {
        this.id = UUID.randomUUID();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.createdAt = LocalDateTime.now();
        }
        this.isActive = true;
    }

    public String getFullName() {
        return String.format("%s %s %s", lastName, firstName, patronymic).trim();
    }

    public abstract UserRole getRole();

    public void updateLastLogin() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.lastLoginAt = LocalDateTime.now();
        }
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }
    public enum UserRole {
        STUDENT,
        TEACHER,
        ADMIN
    }
}

