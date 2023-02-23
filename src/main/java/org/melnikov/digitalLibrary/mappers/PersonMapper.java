package org.melnikov.digitalLibrary.mappers;

import org.melnikov.digitalLibrary.models.Person;
import org.springframework.jdbc.core.RowMapper;


import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * @author Nikolay Melnikov
 */
public class PersonMapper implements RowMapper<Person> {

    @Override
    public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
        Person person = new Person();

        person.setId(rs.getInt("id"));
        person.setFullName(rs.getString("full_name"));
        person.setYearOfBirth(rs.getInt("year_of_birth"));


        return person;
    }
}
