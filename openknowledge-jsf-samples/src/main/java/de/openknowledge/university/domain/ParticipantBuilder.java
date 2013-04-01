package de.openknowledge.university.domain;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named
@RequestScoped
public class ParticipantBuilder {

  private String firstName;
  private String lastName;
  private String streetName;
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
  
  public ParticipantBuilder livingInStreet(String name, String number) {
    return livingInStreet(new StreetBuilder().withName(name).andNumber(number));
  }
  
  public StreetBuilder getStreet() {
    return new StreetBuilder();
  }
  
  private ParticipantBuilder livingInStreet(StreetBuilder streetBuilder) {
    this.streetName = streetBuilder.getName();
    this.streetNumber = streetBuilder.getNumber();
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
    new Address(participant, new Street(streetName, streetNumber), new City(zipCode, city));
    return participant;
  }

  public class StreetBuilder {
    
    public String getName() {
      return streetName;
    }

    public String getNumber() {
      return streetNumber;
    }

    public StreetBuilder withName(String name) {
      streetName = name;
      return this;
    }

    public StreetBuilder andNumber(String number) {
      streetNumber = number;
      return this;
    }

    Street build() {
      return new Street(streetName, streetNumber);
    }
  }
}
