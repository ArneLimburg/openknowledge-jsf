package de.openknowledge.university.domain;

import static org.apache.commons.lang3.Validate.notNull;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Address {

	@Id
	@GeneratedValue
	private int id;
	@ManyToOne
	private Participant participant;
	@Embedded
	@AttributeOverride(name = "name", column = @Column(name = "street"))
	private Street street;
	@Embedded
  @AttributeOverride(name = "name", column = @Column(name = "city"))
	private City city;

	protected Address() {
	  // for jpa
	}

	public Address(Participant participant, Street street, City city) {
	  this.participant = notNull(participant, "participant may not be null");
	  this.street = notNull(street, "street may not be null");
	  this.city = notNull(city, "city may not be null");
	  participant.addAddress(this);
	}

	public int getId() {
    return id;
  }

	public Participant getParticipant() {
	  return participant;
	}
	public Street getStreet() {
    return street;
  }

  public City getCity() {
    return city;
  }
}
