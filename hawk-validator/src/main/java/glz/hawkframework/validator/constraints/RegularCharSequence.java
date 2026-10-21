package glz.hawkframework.validator.constraints;

import glz.hawkframework.validator.validators.RegularCharSequenceValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Check the validated value must match the required {@link #pattern() pattern}
 *
 * @author Zhang Peng
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(RegularCharSequence.List.class)
@Documented
@Constraint(validatedBy = {RegularCharSequenceValidator.class})
public @interface RegularCharSequence {

    String DEFAULT_MESSAGE_TEMPLATE = "{glz.hawkframework.validator.constraints.RegularCharSequence.%s.message}";

    String message() default DEFAULT_MESSAGE_TEMPLATE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     *
     * @return the pattern is used to validate value
     */
    CommonPattern pattern();

    /**
     * Defines several {@link RegularCharSequence} annotations on the same element.
     *
     * @see RegularCharSequence
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        RegularCharSequence[] value();
    }


    enum CommonPattern {

        /**
         * ASCII
         */
        ASCII_PATTERN("[\\x00-\\x7F]*"),
        /**
         * DIGIT
         */
        DIGIT_PATTERN("[0-9]*"),
        /**
         * ALPHABET, CASE INSENSITIVE
         */
        ALPHABET_PATTERN("[A-Za-z]*"),
        /**
         * UPPERCASE ALPHABET
         */
        UPPERCASE_ALPHABET_PATTERN("[A-Z]*"),
        /**
         * LOWERCASE ALPHABET
         */
        LOWERCASE_ALPHABET_PATTERN("[a-z]*"),
        /**
         * DIGIT and CASE INSENSITIVE ALPHABET
         */
        DIGIT_ALPHABET_PATTERN("[0-9A-Za-z]*"),
        /**
         * DIGIT and UPPERCASE ALPHABET
         */
        DIGIT_UPPERCASE_ALPHABET_PATTERN("[0-9A-Z]*"),
        /**
         * DIGIT and LOWERCASE ALPHABET
         */
        DIGIT_LOWERCASE_ALPHABET_PATTERN("[0-9a-z]*");

        private final Pattern pattern;

        CommonPattern(String patternString) {
            this.pattern = Pattern.compile(patternString);
        }

        public Pattern getPattern() {
            return pattern;
        }

        public boolean isMatch(CharSequence value) {
            return pattern.matcher(value).matches();
        }

    }

}
