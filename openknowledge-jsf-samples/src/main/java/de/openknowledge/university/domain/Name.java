package de.openknowledge.university.domain;

import static org.apache.commons.lang3.Validate.notNull;

import javax.persistence.Embeddable;

@Embeddable
public class Name {

  private String first;
  private String last;

  protected Name() {
    // for jpa
  }
  
  public Name(String firstName, String lastName) {
    first = notNull(firstName, "first name may not be null");
    last = notNull(lastName, "last name may not be null");
  }

  public String getFirst() {
    return first;
  }
  
  public String getLast() {
    return last;
  }

  @Override
  public String toString() {
    return first + ' ' + last;
  }

  @Override
  public int hashCode() {
    return first.hashCode() ^ last.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof Name)) {
      return false;
    }
    Name name = (Name)object;
    return first.equals(name.getFirst()) && last.equals(name.getLast());
  }
}
