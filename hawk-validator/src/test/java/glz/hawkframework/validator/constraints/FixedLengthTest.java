package glz.hawkframework.validator.constraints;

import glz.hawkframework.validator.constraints.config.TestConfig;
import glz.hawkframework.validator.constraints.support.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = TestConfig.class)
public class FixedLengthTest {
    @Autowired
    private Validator validator;

    @BeforeEach
    public void setup(){
        LocaleContextHolder.setDefaultLocale(Locale.CHINA);
    }


    @Test
    public void has_one_error() {
        Book book = new Book();
        book.setFixedLengthName("123456");
        Set<ConstraintViolation<Book>> set = validator.validate(book);
        assertThat(set).hasSize(1);
        assertThat(set.iterator().next().getMessage()).isEqualTo("长度必须是5");
    }

    @Test
    public void has_no_error() {
        Book book = new Book();
        book.setFixedLengthName("12345");
        Set<ConstraintViolation<Book>> set = validator.validate(book);
        assertThat(set).hasSize(0);
    }
}
