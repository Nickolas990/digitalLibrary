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
public class BooksRepository implements ListCrudRepository<Book, Integer> {

    private final JdbcTemplate jdbcTemplate;

    private final SessionFactory sessionFactory;



    @Autowired
    public BooksRepository(JdbcTemplate jdbcTemplate, SessionFactory sessionFactory) {
        this.jdbcTemplate = jdbcTemplate;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public <S extends Book> S save(S book) {

        try (Session session = sessionFactory.openSession()) {
           session.beginTransaction();
           session.persist(book);
           session.getTransaction().commit();
           return book;
       }
    }

    @Override
    public <S extends Book> List<S> saveAll(Iterable<S> entities) {
        List<S> books = new ArrayList<>((Collection<S>) entities);
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            books.forEach(session::persist);
            session.getTransaction().commit();
        }
        return books;
    }

    public void update(int id, Book updatedBook) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Book book = session.get(Book.class, id);
            book.setTitle(updatedBook.getTitle());
            book.setAuthor(updatedBook.getAuthor());
            book.setYearOfPublication(updatedBook.getYearOfPublication());
            session.refresh(book);
            session.getTransaction().commit();
        }
    }

    @Override
    public List<Book> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Book", Book.class).getResultList();
        }
    }

    @Override
    public List<Book> findAllById(Iterable<Integer> ints) {

        return jdbcTemplate.query("SELECT * FROM book WHERE id =?", new BookMapper(), ints);
    }

    @Override
    public Optional<Book> findById(Integer id) {
        return jdbcTemplate.query("SELECT * FROM book WHERE id = ?", new BookMapper(), id)
                .stream()
                .findFirst();
    }

    @Override
    public boolean existsById(Integer id) {
        return jdbcTemplate.query("SELECT * FROM book WHERE id = ?", new BookMapper(), id)
                .stream()
                .findFirst()
                .isPresent();
    }

    @Override
    public long count() {
        return findAll().size();
    }

    @Override
    public void deleteById(Integer id) {
        jdbcTemplate.update("DELETE FROM book WHERE id =?", id);
    }

    @Override
    public void delete(Book entity) {
        jdbcTemplate.update("DELETE FROM book WHERE title = ? AND author = ?", entity.getTitle(), entity.getAuthor());
    }

    @Override
    public void deleteAllById(Iterable<? extends Integer> ints) {
        List<Integer> ids = new ArrayList<>((Collection) ints);
        jdbcTemplate.batchUpdate("DELETE FROM book WHERE id =?", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, ints.iterator().next());
            }

            @Override
            public int getBatchSize() {
                return ids.size();
            }
        });
    }

    @Override
    public void deleteAll(Iterable<? extends Book> entities) {
        List<Book> books = new ArrayList<>((Collection) entities);
        jdbcTemplate.batchUpdate("DELETE FROM person WHERE id = ?", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, entities.iterator().next().getId());
            }

            @Override
            public int getBatchSize() {
                return books.size();
            }
        });
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("TRUNCATE book");
    }

    public void release(int id) {
        jdbcTemplate.update("UPDATE book SET person_id = NULL WHERE id =?", id);
    }

    public Optional<Person> getOwner(Integer id) {
        return jdbcTemplate.query("SELECT person.* FROM book " +
                        "JOIN person ON book.person_id = person.id where book.id=?",
                        new PersonMapper(), id)
                .stream()
                .findAny();
    }

    public void assign(int id, Person selectedPerson) {
        jdbcTemplate.update("UPDATE book SET person_id =? WHERE id =?", selectedPerson.getId(), id);
    }
}
