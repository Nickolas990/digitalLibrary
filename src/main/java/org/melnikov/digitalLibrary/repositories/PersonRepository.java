package org.melnikov.digitalLibrary.repositories;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.melnikov.digitalLibrary.mappers.BookMapper;
import org.melnikov.digitalLibrary.mappers.PersonMapper;
import org.melnikov.digitalLibrary.models.Book;
import org.melnikov.digitalLibrary.models.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author Nikolay Melnikov
 */
@Component
public class PersonRepository implements ListCrudRepository<Person, Integer> {

    private final JdbcTemplate jdbcTemplate;
    private final SessionFactory sessionFactory;

    @Autowired
    public PersonRepository(JdbcTemplate jdbcTemplate, SessionFactory sessionFactory) {
        this.jdbcTemplate = jdbcTemplate;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public <S extends Person> S save(S person) {

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(person);
            session.getTransaction().commit();
        }
        return person;
    }

    @Override
    public <S extends Person> List<S> saveAll(Iterable<S> entities) {
        List<S> people = new ArrayList<>((Collection<S>) entities);
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            people.forEach(session::persist);
            session.getTransaction().commit();
        }
//        jdbcTemplate.batchUpdate("INSERT INTO person (full_name, year_of_birth) VALUES(?,?)", new BatchPreparedStatementSetter() {
//            @Override
//            public void setValues(PreparedStatement ps, int i) throws SQLException {
//                ps.setString(1, people.get(i).getFullName());
//                ps.setInt(2, people.get(i).getYearOfBirth());
//            }
//
//            @Override
//            public int getBatchSize() {
//                return people.size();
//            }
//        });
        return people;
    }

    @Override
    public Optional<Person> findById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            Person person = session.get(Person.class, id);
            return Optional.ofNullable(person);
        }
    }

    @Override
    public boolean existsById(Integer id) {
//        return jdbcTemplate.query("SELECT * FROM person WHERE id = ?", new PersonMapper(), id)
//                .stream()
//                .findFirst()
//                .isPresent();
        try(Session session = sessionFactory.openSession()) {
            Person person = session.get(Person.class, id);
            return  Optional.ofNullable(person).isPresent();
        }
    }

    @Override
    public List<Person> findAll() {
      try (Session session = sessionFactory.openSession()) {
          return session.createQuery("from Person p", Person.class).getResultList();
      }

//        return jdbcTemplate.query("SELECT * FROM person", new PersonMapper());
    }

    @Override
    public List<Person> findAllById(Iterable<Integer> ids) {
//        return jdbcTemplate.query("SELECT * FROM person WHERE id =?", new PersonMapper(), ints);
        try(Session session = sessionFactory.openSession()) {
            return session.createQuery("from Person p where p.id in (:ids)", Person.class)
                    .setParameter("ids", ids)
                    .getResultList();
        }
    }

    public Optional<Person> findByName(String fullName) {
        try(Session session = sessionFactory.openSession()) {
            return session.createQuery("from Person p where p.fullName = :fullName", Person.class)
                    .setParameter("fullName", fullName)
                    .stream()
                    .findFirst();
        }
//        return jdbcTemplate.query("SELECT * FROM person WHERE full_name =?", new PersonMapper(), fullName)
//                .stream()
//                .findFirst();

    }

    @Override
    public long count() {
//        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM person", Long.class);
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select count(p) from Person p", Long.class)
                    .getResultStream()
                    .count();
        }
    }

    @Override
    public void deleteById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Person person = session.get(Person.class, id);
            session.delete(person);
            session.getTransaction().commit();
        }
//        jdbcTemplate.update("DELETE FROM person WHERE id =?", id);

    }

    @Override
    public void delete(Person personToDelete) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Person person = session.get(Person.class, personToDelete.getId());
            session.remove(person);
            session.getTransaction().commit();
        }

//        jdbcTemplate.update("DELETE FROM person WHERE full_name =?", personToDelete.getFullName());
    }

    @Override
    public void deleteAllById(Iterable<? extends Integer> ints) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createQuery("delete from Person p where p.id in (:ids)", Person.class)
                    .setParameter("ids", ints);
        }

//        List<Integer> ids = new ArrayList<>((Collection) ints);
//        jdbcTemplate.batchUpdate("DELETE FROM person WHERE id =?", new BatchPreparedStatementSetter() {
//            @Override
//            public void setValues(PreparedStatement ps, int i) throws SQLException {
//                ps.setLong(1, ints.iterator().next());
//            }
//
//            @Override
//            public int getBatchSize() {
//                return ids.size();
//            }
//        });

    }

    @Override
    public void deleteAll(Iterable<? extends Person> entities) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            for (Person person : entities) {
                session.createQuery("delete from Person p where p.id = :id", Person.class)
                        .setParameter("id", person.getId())
                        .executeUpdate();
            }
        }
//        jdbcTemplate.batchUpdate("DELETE FROM person WHERE id = ?", new BatchPreparedStatementSetter() {
//            @Override
//            public void setValues(PreparedStatement ps, int i) throws SQLException {
//                ps.setLong(1, entities.iterator().next().getId());
//            }
//
//            @Override
//            public int getBatchSize() {
//                return people.size();
//            }
//        });
    }

    @Override
    public void deleteAll() {
//        jdbcTemplate.update("TRUNCATE person");
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createQuery("delete from Person p").executeUpdate();
            session.getTransaction().commit();
        }
    }


    public void update(int id, Person updatedPerson) {
//        jdbcTemplate.update("UPDATE person SET full_name =?, year_of_birth = ? WHERE id = ?",
//                updatedPerson.getFullName(), updatedPerson.getYearOfBirth(), id);

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createQuery("update Person p set p.fullName = :fullName, p.yearOfBirth = :yearOfBirth where p.id = :id", Person.class)
                    .setParameter("fullName", updatedPerson.getFullName())
                    .setParameter("yearOfBirth", updatedPerson.getYearOfBirth())
                    .setParameter("id", id);
        }
    }

    public List<Book> getBooksByPersonId(int id) {
        return jdbcTemplate.query("SELECT * FROM book WHERE person_id =?", new BookMapper(), id);
    }
}
