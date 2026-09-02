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
public class RegularCharSequenceTest {

    @Autowired
    private Validator validator;

    @BeforeEach
    public void setup(){
        LocaleContextHolder.setDefaultLocale(Locale.CHINA);
    }

    @Test
    public void has_one_error_from_digit_pattern() {
        Book book = new Book();
        book.setDigitPattern("abd1");
        Set<ConstraintViolation<Book>> set = validator.validate(book);
        assertThat(set).hasSize(1);
        assertThat(set.iterator().next().getMessage()).isEqualTo("必须全部是数字");
    }

    @Test
    public void has_no_error_from_digit_pattern() {
        Book book = new Book();
        book.setDigitPattern("11111");
        Set<ConstraintViolation<Book>> set = validator.validate(book);
        assertThat(set).hasSize(0);
    }

    @Test
    public void has_one_error_from_digit_lower_case_alphabet_pattern() {
        Book book = new Book();
        book.setDigitLowercaseAlphabetPattern("Ad13df");
        Set<ConstraintViolation<Book>> set = validator.validate(book);
        assertThat(set).hasSize(1);
        assertThat(set.iterator().next().getMessage()).isEqualTo("必须是小写英文字母和数字的任意组合");
    }

    @Test
    public void has_no_error_from_digit_lower_case_alphabet_pattern() {
        Book book = new Book();
        book.setDigitLowercaseAlphabetPattern("ad13df");
        Set<ConstraintViolation<Book>> set = validator.validate(book);
        assertThat(set).hasSize(0);
    }
}
