package de.openknowledge.university.domain;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Named
@Stateless
public class ParticipantRepository {

  @PersistenceContext
  private EntityManager entityManager;

  public void create(ParticipantBuilder participantBuilder) {
    entityManager.persist(participantBuilder.build());
  }

  public List<Participant> findAll() {
    return entityManager.createNamedQuery("Participant.findAll", Participant.class).getResultList();
  }
}
