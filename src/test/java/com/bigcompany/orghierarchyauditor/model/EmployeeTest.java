package com.bigcompany.orghierarchyauditor.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {

    @Test
    void testAllArgsConstructor() {
        Employee subordinate = new Employee(2, "Sub", "Ordinate", 30000.0, 1, new ArrayList<>());
        List<Employee> subs = new ArrayList<>();
        subs.add(subordinate);

        Employee emp = new Employee(1, "John", "Doe", 50000.0, null, subs);

        // Access all fields
        assertEquals(1, emp.getId());
        assertEquals("John", emp.getFirstName());
        assertEquals("Doe", emp.getLastName());
        assertEquals(50000.0, emp.getSalary());
        assertNull(emp.getManagerId());
        assertEquals(1, emp.getSubordinates().size());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        Employee emp = new Employee();

        emp.setId(10);
        emp.setFirstName("Alice");
        emp.setLastName("Smith");
        emp.setSalary(60000.0);
        emp.setManagerId(5);

        List<Employee> subs = new ArrayList<>();
        subs.add(new Employee(11, "Bob", "Brown", 30000.0, 10, new ArrayList<>()));
        emp.setSubordinates(subs);

        assertEquals(10, emp.getId());
        assertEquals("Alice", emp.getFirstName());
        assertEquals("Smith", emp.getLastName());
        assertEquals(60000.0, emp.getSalary());
        assertEquals(5, emp.getManagerId());
        assertEquals(1, emp.getSubordinates().size());
    }

    @Test
    void testEqualsAndHashCodeAndCanDiffer() {
        Employee e1 = new Employee(1, "A", "B", 100.0, null, new ArrayList<>());
        Employee e2 = new Employee(1, "A", "B", 100.0, null, new ArrayList<>());

        assertEquals(e1, e2);
        assertEquals(e1.hashCode(), e2.hashCode());

        Employee e3 = new Employee(2, "C", "D", 200.0, null, new ArrayList<>());
        assertNotEquals(e1, e3);
        assertNotEquals(e1.hashCode(), e3.hashCode());
    }

    @Test
    void testToStringContainsAllFields() {
        Employee emp = new Employee(1, "Tom", "Hardy", 75000.0, null, new ArrayList<>());
        String str = emp.toString();

        assertTrue(str.contains("Tom"));
        assertTrue(str.contains("Hardy"));
        assertTrue(str.contains("75000.0"));
        assertTrue(str.contains("subordinates"));
    }

    @Test
    void testSubordinatesManipulation() {
        Employee manager = new Employee();
        Employee s1 = new Employee(2, "Sub1", "Last1", 30000.0, 1, new ArrayList<>());
        Employee s2 = new Employee(3, "Sub2", "Last2", 32000.0, 1, new ArrayList<>());

        List<Employee> subList = manager.getSubordinates();
        subList.add(s1);
        subList.add(s2);
        assertEquals(2, manager.getSubordinates().size());
    }

    @Test
    void testSetterWithNullManagerId() {
        Employee emp = new Employee();
        emp.setManagerId(null);
        assertNull(emp.getManagerId());
    }
}