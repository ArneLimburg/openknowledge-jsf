package de.openknowledge.university.domain;

import static org.apache.commons.lang3.Validate.notNull;

import javax.persistence.Embeddable;

@Embeddable
public class City {

  private String zipCode;
  private String name;

  protected City() {
    // for jpa
  }
  
  public City(String zip, String cityName) {
    zipCode = notNull(zip, "zip code may not be null");
    name = notNull(cityName, "city name may not be null");
  }

  public String getZipCode() {
    return zipCode;
  }
  
  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return zipCode + ' ' + name;
  }

  @Override
  public int hashCode() {
    return zipCode.hashCode() ^ name.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof City)) {
      return false;
    }
    City city = (City)object;
    return zipCode.equals(city.getZipCode()) && name.equals(city.getName());
  }
}
