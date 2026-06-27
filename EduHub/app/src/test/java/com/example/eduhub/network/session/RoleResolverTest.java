package com.example.eduhub.network.session;

import org.junit.Test;

import static org.junit.Assert.*;

public class RoleResolverTest {

    @Test
    public void isNavigableRole_returnsTrueForTeacher() {
        assertTrue(RoleResolver.isNavigableRole("TEACHER"));
    }

    @Test
    public void isNavigableRole_returnsTrueForStudent() {
        assertTrue(RoleResolver.isNavigableRole("STUDENT"));
    }

    @Test
    public void isNavigableRole_returnsTrueForCombined() {
        assertTrue(RoleResolver.isNavigableRole("TEACHER,STUDENT"));
    }

    @Test
    public void isNavigableRole_returnsTrueForStudentTeacher() {
        assertTrue(RoleResolver.isNavigableRole("STUDENT,TEACHER"));
    }

    @Test
    public void isNavigableRole_returnsTrueCaseInsensitive() {
        assertTrue(RoleResolver.isNavigableRole("teacher"));
    }

    @Test
    public void isNavigableRole_returnsFalseForNull() {
        assertFalse(RoleResolver.isNavigableRole(null));
    }

    @Test
    public void isNavigableRole_returnsFalseForEmpty() {
        assertFalse(RoleResolver.isNavigableRole(""));
    }

    @Test
    public void isNavigableRole_returnsFalseForUnknownRole() {
        assertFalse(RoleResolver.isNavigableRole("ADMIN"));
    }

    @Test
    public void isNavigableRole_returnsFalseForBlank() {
        assertFalse(RoleResolver.isNavigableRole("   "));
    }
}
