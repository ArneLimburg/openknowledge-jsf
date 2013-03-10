package de.openknowledge.university.domain;

import javax.inject.Named;

@Named
public class ParticipantBuilder {

  private String firstName;
  private String lastName;
  private String street;
  private String streetNumber;
  private String zipCode;
  private String city;

  public String getFirstName() {
    return firstName;
  }
  
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
  
  public String getLastName() {
    return lastName;
  }
  
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
  
  public String getStreet() {
    return street;
  }
  
  public void setStreet(String street) {
    this.street = street;
  }
  
  public String getStreetNumber() {
    return streetNumber;
  }
  
  public void setStreetNumber(String streetNumber) {
    this.streetNumber = streetNumber;
  }
  
  public String getZipCode() {
    return zipCode;
  }
  
  public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
  }
  
  public String getCity() {
    return city;
  }
  
  public void setCity(String city) {
    this.city = city;
  }

  public Participant build() {
    Participant participant = new Participant(new Name(firstName, lastName));
    Address address = new Address(participant, new Street(street, streetNumber), new City(zipCode, city));
    return participant;
  }
}
