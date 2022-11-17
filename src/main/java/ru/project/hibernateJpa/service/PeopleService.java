package ru.project.hibernateJpa.service;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.project.hibernateJpa.model.Book;
import ru.project.hibernateJpa.model.Person;
import ru.project.hibernateJpa.repository.BookRepository;
import ru.project.hibernateJpa.repository.PeopleRepository;

import java.util.*;

@Service
@Transactional(readOnly = true)
// все публичные методы класса будут такими
public class PeopleService {

    private final int delayPeriod = 864_000_000;

    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public List<Person> findAll() {
        return peopleRepository.findAll();
    }

    public Person findOne(int id) {
        Optional<Person> foundPerson = peopleRepository.findById(id);
        return foundPerson.orElse(null);
    }

    @Transactional
    public void save(Person person) {
        peopleRepository.save(person);
    }

    @Transactional
    public void update(int id, Person updatedPerson) {
        updatedPerson.setId(id);
        peopleRepository.save(updatedPerson);
    }

    @Transactional
    public void delete(int id) {
        peopleRepository.deleteById(id);
    }

    public List<Book> getBooksByPersonId(int id){
        Optional<Person> person = peopleRepository.findById(id);

        if(person.isPresent()){
            Hibernate.initialize(person.get().getBooks());
            //книги будут подгружены, но на всякий случай вызвать Hibernate.initialize()
            person.get().getBooks().forEach(this::checkDelay);
            return person.get().getBooks();
        }
        else {
            return Collections.emptyList();
        }
    }

    public Optional <Person> getPersonByName (String name) {
        return peopleRepository.findByName(name);
    }

    public void checkDelay(Book book) {
        if(new Date().getTime() - book.getTimeAt().getTime()> delayPeriod){
            book.setExpired(true);
        }
    }
}
