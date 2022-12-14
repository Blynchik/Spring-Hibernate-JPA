package ru.project.hibernateJpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.project.hibernateJpa.model.Book;
import ru.project.hibernateJpa.model.Person;
import ru.project.hibernateJpa.repository.BookRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findAll(boolean sortByYear) {
        if (sortByYear) {
            return bookRepository.findAll(Sort.by("year"));//сортировка по полю
        } else {
            return bookRepository.findAll();
        }
    }

    public List<Book> paging(Integer page, Integer booksPerPage, boolean sortByYear) {
        if (sortByYear) {
            return bookRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by("year"))).getContent();//сортировка и пагинация
        } else {
            return bookRepository.findAll(PageRequest.of(page, booksPerPage)).getContent(); //добавление пагинации
        }
    }

        public Book findOne ( int id){
            Optional<Book> foundBook = bookRepository.findById(id);
            return foundBook.orElse(null);
        }

        @Transactional
        public void save (Book book){
            bookRepository.save(book);
        }

        @Transactional
        public void update ( int id, Book updatedBook){
            Book bookToBeUpdated = bookRepository.findById(id).get();
            updatedBook.setId(id);
            updatedBook.setOwner(bookToBeUpdated.getOwner());//т.к. сохраняется книга без пользователя
            bookRepository.save(updatedBook);
        }

        @Transactional
        public void delete ( int id){
            bookRepository.deleteById(id);
        }


        public Optional<Person> getBookOwner ( int id){
            return bookRepository.findById(id).map(Book::getOwner);
        }

        @Transactional
        public void release ( int id){
            Book releasedBook = bookRepository.findById(id).get();
            releasedBook.setOwner(null);
            releasedBook.setTimeAt(null);
            bookRepository.save(releasedBook);
        }

        @Transactional
        public void assign ( int id, Person selectedPerson){
            Book book = bookRepository.findById(id).get();
            book.setOwner(selectedPerson);
            book.setTimeAt(new Date());
            bookRepository.save(book);
        }


    public List<Book> searchByTitle(String query) {
        return bookRepository.findByTitleStartingWith(query);
    }
}
