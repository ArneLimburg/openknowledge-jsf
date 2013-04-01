package de.openknowledge.university.domain;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

@Entity
@NamedQuery(name = "Participant.findAll", query = "SELECT p FROM Participant p LEFT OUTER JOIN FETCH p.addresses")
public class Participant {

	@Id
	@GeneratedValue
	private int id;
	@Embedded
	private Name name;
	@OneToMany(mappedBy = "participant", orphanRemoval = true, cascade = CascadeType.PERSIST)
	private Set<Address> addresses;

	protected Participant() {
	  // for jpa
	}

	public Participant(Name name) {
	  this.name = notNull(name, "name may not be null");
	  this.addresses = new HashSet<Address>();
	}
	
	public int getId() {
	  return id;
	}

	public Name getName() {
    return name;
  }

	public void clearAddresses() {
	  addresses.clear();
	}

	public Address getFirstAddress() {
	  return addresses.iterator().next();
	}

	public Set<Address> getAddresses() {
    return Collections.unmodifiableSet(addresses);
  }

	void addAddress(Address address) {
	  isTrue(this == address.getParticipant());
	  addresses.add(address);
	}
}
