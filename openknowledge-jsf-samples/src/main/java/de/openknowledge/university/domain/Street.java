package de.openknowledge.university.domain;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

import javax.persistence.Embeddable;

@Embeddable
public class Street {

  private String name;
  private String number;

  protected Street() {
    // for jpa
  }
  
  public Street(String streetName, String streetNumber) {
    name = notBlank(streetName, "street name may not be null");
    number = notBlank(streetNumber, "street number may not be null");
  }

  public String getName() {
    return name;
  }
  
  public Street newName(String name) {
    return new Street(name, number);
  }

  public String getNumber() {
    return number;
  }
  
  public Street newNumber(String number) {
    return new Street(name, number);
  }

  @Override
  public String toString() {
    return name + ' ' + number;
  }

  @Override
  public int hashCode() {
    return name.hashCode() ^ number.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof Street)) {
      return false;
    }
    Street street = (Street)object;
    return name.equals(street.getName()) && number.equals(street.getNumber());
  }
}
