package com.medivault.model;

/**
 * Patient.java — Pure Model Class (POJO)
 */
public class Patient {

    private int id;
    private String name;
    private int age;
    private String contact;
    private String disease;
    private String assignedDoctor;
    private String status;

    public Patient() {}

    /**
     * Constructor used when adding a new patient (DB auto-generates the ID).
     */
    public Patient(String name, int age, String contact, String disease, String assignedDoctor) {
        this.name = name;
        this.age = age;
        this.contact = contact;
        this.disease = disease;
        this.assignedDoctor = assignedDoctor;
        this.status = "Stable";
    }

    /**
     * Constructor used when reading a patient back from the DB (ID is known).
     */
    public Patient(int id, String name, int age, String contact, String disease, String assignedDoctor, String status) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.contact = contact;
        this.disease = disease;
        this.assignedDoctor = assignedDoctor;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getDisease() { return disease; }
    public void setDisease(String disease) { this.disease = disease; }

    public String getAssignedDoctor() { return assignedDoctor; }
    public void setAssignedDoctor(String assignedDoctor) { this.assignedDoctor = assignedDoctor; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Patient{id=" + id + ", name='" + name + "', disease='" + disease + "'}";
    }
}