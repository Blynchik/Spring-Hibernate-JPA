package ru.project.hibernateJpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.project.hibernateJpa.model.Book;
import ru.project.hibernateJpa.model.Person;
import ru.project.hibernateJpa.service.BookService;
import ru.project.hibernateJpa.service.PeopleService;
import ru.project.hibernateJpa.utils.BookValidator;

import javax.validation.Valid;
import java.util.Optional;

@Controller
//это контроллер
@RequestMapping("/books")
//дефолтный запрос
public class BookController {
    private final BookService bookService;
    private final PeopleService peopleService;
    private final BookValidator bookValidator;

    @Autowired
    public BookController(BookService bookService, PeopleService peopleService, BookValidator bookValidator) {
        this.bookService = bookService;
        this.peopleService = peopleService;
        this.bookValidator = bookValidator;
    }

    @GetMapping
    /*получаем по какому запросу, сейчас по дефолтному*/
    public String showAll(Model model,
                          @RequestParam(value = "page", required = false) Integer page,
                          //передаваемое значение в запросе, page - имя в отображение, его может и не быть
                          @RequestParam(value = "books_per_page", required = false) Integer booksPerPage,
                          @RequestParam(value = "sort_by_year", required = false) boolean sortByYear) {

        if (page == null || booksPerPage == null) {
            model.addAttribute("books"/*как обратиться на странице отображения*/,
                    bookService.findAll(sortByYear))/*что получим при обращении*/;
        } else {
            model.addAttribute("books", bookService.paging(page, booksPerPage,sortByYear));
        }

        return "books/all";
        /*на какую страницу переход*/
    }

    @GetMapping("/{id}")
    /*получаем по какому запросу, сейчас по id*/
    public String showOne(@PathVariable("id") /*обращение может меняться*/ int id, Model model,
                          @ModelAttribute("person"/*какую переменную внедрить на страницу отобраения*/)
                          Person person /*что внедряем*/) {
        model.addAttribute("book", bookService.findOne(id));

        Optional<Person> bookOwner = bookService.getBookOwner(id);

        if (bookOwner.isPresent()) {
            model.addAttribute("owner", bookOwner.get());
            //если у книга у человека, показываем его
        } else {
            model.addAttribute("people", peopleService.findAll());
            //если нет, то передаем список людей, для выбора из списка
        }

        return "books/one";
    }

    @GetMapping("/new")
    /*получаем*/
    public String newBook(@ModelAttribute("book") Book book) {
        return "/books/new";
    }

    @PostMapping()
    /*добавляем на сервере*/
    public String create(@ModelAttribute("book") @Valid Book book,
                         BindingResult bindingResult/*если будет ошибка, она поместиться сюда*/) {

        bookValidator.validate(book, bindingResult);

        if (bindingResult.hasErrors()) {//если есть ошибка будет выполнено...
            return "books/new";
        }

        bookService.save(book);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("book", bookService.findOne(id));
        return "books/edit";
    }

    @PatchMapping("/{id}")
    /*изменяем на сервере*/
    public String update(@ModelAttribute("book") @Valid Book book,
                         BindingResult bindingResult,
                         @PathVariable("id") int id) {

        bookValidator.validate(book, bindingResult);

        if (bindingResult.hasErrors()) {
            return "books/edit";
        }

        bookService.update(id, book);
        return "redirect:/books";
    }

    @DeleteMapping("/{id}")
    /*удаялем на сервере*/
    public String delete(@PathVariable("id") int id) {
        bookService.delete(id);
        return "redirect:/books";
    }

    @PatchMapping("/{id}/release")
    public String release(@PathVariable("id") int id) {
        bookService.release(id);
        return "redirect:/books/" + id;
    }

    @PatchMapping("/{id}/assign")
    public String assign(@PathVariable("id") int id, @ModelAttribute("person") Person selectedPerson) {
        bookService.assign(id, selectedPerson);
        return "redirect:/books/" + id;
    }
}