package org.melnikov.digitalLibrary.repositories;

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

    @Autowired
    public PersonRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <S extends Person> S save(S person) {
        jdbcTemplate.update("INSERT INTO person (full_name, year_of_birth) VALUES(?,?)",
                person.getFullName(), person.getYearOfBirth());
        return person;
    }

    @Override
    public <S extends Person> List<S> saveAll(Iterable<S> entities) {
        List<S> people = new ArrayList<>((Collection<S>) entities);
        jdbcTemplate.batchUpdate("INSERT INTO person (full_name, year_of_birth) VALUES(?,?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, people.get(i).getFullName());
                ps.setInt(2, people.get(i).getYearOfBirth());
            }

            @Override
            public int getBatchSize() {
                return people.size();
            }
        });
        return people;
    }

    @Override
    public Optional<Person> findById(Integer id) {
        return jdbcTemplate.query("SELECT * FROM person WHERE id = ?", new PersonMapper(), id)
                .stream()
                .findFirst();
    }

    @Override
    public boolean existsById(Integer id) {
        return jdbcTemplate.query("SELECT * FROM person WHERE id = ?", new PersonMapper(), id)
                .stream()
                .findFirst()
                .isPresent();
    }

    @Override
    public List<Person> findAll() {
        return jdbcTemplate.query("SELECT * FROM person", new PersonMapper());
    }

    @Override
    public List<Person> findAllById(Iterable<Integer> ints) {
        return jdbcTemplate.query("SELECT * FROM person WHERE id =?", new PersonMapper(), ints);
    }

    public Optional<Person> findByName(String fullName) {
        return jdbcTemplate.query("SELECT * FROM person WHERE full_name =?", new PersonMapper(), fullName)
                .stream()
                .findFirst();

    }

    @Override
    public long count() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM person", Long.class);
    }

    @Override
    public void deleteById(Integer id) {
        jdbcTemplate.update("DELETE FROM person WHERE id =?", id);

    }

    @Override
    public void delete(Person entity) {
        jdbcTemplate.update("DELETE FROM person WHERE full_name =?", entity.getFullName());
    }

    @Override
    public void deleteAllById(Iterable<? extends Integer> ints) {
        List<Integer> ids = new ArrayList<>((Collection) ints);
        jdbcTemplate.batchUpdate("DELETE FROM person WHERE id =?", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, ints.iterator().next());
            }

            @Override
            public int getBatchSize() {
                return ids.size();
            }
        });

    }

    @Override
    public void deleteAll(Iterable<? extends Person> entities) {
        List<Person> people = new ArrayList<>((Collection) entities);
        jdbcTemplate.batchUpdate("DELETE FROM person WHERE id = ?", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, entities.iterator().next().getId());
            }

            @Override
            public int getBatchSize() {
                return people.size();
            }
        });
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("TRUNCATE person");
    }


    public void update(int id, Person updatedPerson) {
        jdbcTemplate.update("UPDATE person SET full_name =?, year_of_birth = ? WHERE id = ?",
                updatedPerson.getFullName(), updatedPerson.getYearOfBirth(), id);
    }

    public List<Book> getBooksByPersonId(int id) {
        return jdbcTemplate.query("SELECT * FROM book WHERE person_id =?", new BookMapper(), id);
    }
}
