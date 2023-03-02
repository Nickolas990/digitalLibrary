package org.melnikov.digitalLibrary.repositories;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.melnikov.digitalLibrary.models.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author Nikolay Melnikov
 */
@Component
public class PersonRepository implements ListCrudRepository<Person, Integer> {

    private final SessionFactory sessionFactory;

    @Autowired
    public PersonRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional
    public <S extends Person> S save(S person) {

        try (Session session = sessionFactory.openSession()) {
            session.persist(person);
        }
        return person;
    }

    @Override
    @Transactional
    public <S extends Person> List<S> saveAll(Iterable<S> entities) {
        try (Session session = sessionFactory.openSession()) {
            List<S> people = new ArrayList<>((Collection<S>) entities);
            people.forEach(session::persist);
            return people;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Person> findById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            Person person = session.get(Person.class, id);
            Hibernate.initialize(person.getBooks());
            return Optional.ofNullable(person);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        try(Session session = sessionFactory.openSession()) {
            Person person = session.get(Person.class, id);
            return  Optional.ofNullable(person).isPresent();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Person> findAll() {
      try (Session session = sessionFactory.openSession()) {
          return session.createQuery("from Person p", Person.class).getResultList();
      }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Person> findAllById(Iterable<Integer> ids) {
        try(Session session = sessionFactory.openSession()) {
            return session.createQuery("from Person p where p.id in (:ids)", Person.class)
                    .setParameter("ids", ids)
                    .getResultList();
        }
    }

    @Transactional(readOnly = true)
    public Optional<Person> findByName(String fullName) {
        try(Session session = sessionFactory.openSession()) {
            return session.createQuery("from Person p where p.fullName = :fullName", Person.class)
                    .setParameter("fullName", fullName)
                    .stream()
                    .findFirst();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select count(p) from Person p", Long.class)
                    .getSingleResult();
        }
    }

    @Transactional
    public void update(int id, Person updatedPerson) {

        try (Session session = sessionFactory.openSession()) {
            Person person = session.get(Person.class, id);
            person.setFullName(updatedPerson.getFullName());
            person.setYearOfBirth(updatedPerson.getYearOfBirth());
        }
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            Person person = session.get(Person.class, id);
            session.remove(person);
        }

    }

    @Override
    @Transactional
    public void delete(Person personToDelete) {
        try (Session session = sessionFactory.openSession()) {
            Person person = session.get(Person.class, personToDelete.getId());
            session.remove(person);
        }
    }

    @Override
    @Transactional
    public void deleteAllById(Iterable<? extends Integer> ints) {
        try(Session session = sessionFactory.openSession()) {
            session.createQuery("delete from Person p where p.id in (:ids)", Person.class)
                    .setParameter("ids", ints);
        }
    }

    @Override
    @Transactional
    public void deleteAll(Iterable<? extends Person> entities) {
        try(Session session = sessionFactory.openSession()) {
            for (Person person : entities) {
                session.createQuery("delete from Person p where p.id = :id", Person.class)
                        .setParameter("id", person.getId())
                        .executeUpdate();
            }
        }
    }

    @Override
    @Transactional
    public void deleteAll() {
//        jdbcTemplate.update("TRUNCATE person");
        try (Session session = sessionFactory.openSession()) {
            session.createQuery("delete from Person p").executeUpdate();
        }
    }
}
