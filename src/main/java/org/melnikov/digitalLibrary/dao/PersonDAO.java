package org.melnikov.digitalLibrary.dao;

import org.melnikov.digitalLibrary.mappers.PersonMapper;
import org.melnikov.digitalLibrary.models.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Nikolay Melnikov
 */

@Component
public class PersonDAO {
    private final JdbcTemplate jdbcTemplate;



    @Autowired
    public PersonDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Person> index() {
        return jdbcTemplate.query("SELECT * FROM person", new PersonMapper());
    }

    public void save(Person person) {
        jdbcTemplate.update("INSERT INTO person (full_name, year_of_birth) VALUES(?,?)",
                person.getFullName(), person.getYearOfBirth());
    }

    public Person show(int id) {
        return jdbcTemplate.query("SELECT * FROM person WHERE id = ?", new PersonMapper(), id)
                .stream()
                .findFirst()
                .orElse(null);
    }
}
