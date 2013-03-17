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
  
  public ParticipantBuilder withFirstName(String firstName) {
    this.firstName = firstName;
    return this;
  }
  
  public String getLastName() {
    return lastName;
  }
  
  public ParticipantBuilder andLastName(String lastName) {
    this.lastName = lastName;
    return this;
  }
  
  public String getStreet() {
    return street;
  }
  
  public ParticipantBuilder livingInStreet(String street) {
    this.street = street;
    return this;
  }
  
  public String getStreetNumber() {
    return streetNumber;
  }
  
  public ParticipantBuilder withStreetNumber(String streetNumber) {
    this.streetNumber = streetNumber;
    return this;
  }
  
  public String getZipCode() {
    return zipCode;
  }
  
  public ParticipantBuilder withZipCode(String zipCode) {
    this.zipCode = zipCode;
    return this;
  }
  
  public String getCity() {
    return city;
  }
  
  public ParticipantBuilder inCity(String city) {
    this.city = city;
    return this;
  }

  Participant build() {
    Participant participant = new Participant(new Name(firstName, lastName));
    new Address(participant, new Street(street, streetNumber), new City(zipCode, city));
    return participant;
  }
}
