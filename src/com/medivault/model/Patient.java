package com.medivault.model;

/**
 * Patient.java — Model Class (POJO)
 * Represents a patient record. Passed between all three layers.
 * No logic here — just data fields with getters and setters.
 */
public class Patient {

    private int id;
    private String name;
    private int age;
    private String phone;

    public Patient() {}

    // Used when adding a new patient (DB auto-generates the id)
    public Patient(String name, int age, String phone) {
        this.name  = name;
        this.age   = age;
        this.phone = phone;
    }

    // Used when reading a patient back from the DB (id is known)
    public Patient(int id, String name, int age, String phone) {
        this.id    = id;
        this.name  = name;
        this.age   = age;
        this.phone = phone;
    }

    public int    getId()             { return id; }
    public void   setId(int id)       { this.id = id; }
    public String getName()           { return name; }
    public void   setName(String n)   { this.name = n; }
    public int    getAge()            { return age; }
    public void   setAge(int a)       { this.age = a; }
    public String getPhone()          { return phone; }
    public void   setPhone(String p)  { this.phone = p; }

    @Override
    public String toString() {
        return "Patient{id=" + id + ", name='" + name +
               "', age=" + age + ", phone='" + phone + "'}";
    }
}
