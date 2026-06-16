package glz.hawkframework.validator.constraints;

import glz.hawkframework.validator.validators.FixedLengthValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.Map;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Check the validated value's length muse equal to required {@link #value() value}.
 * <p>
 * Supports {@link CharSequence}, {@code Array}, {@link Collection} and {@link Map}
 *
 * @author Hawk
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(FixedLength.List.class)
@Documented
@Constraint(validatedBy = {
    FixedLengthValidator.FixedLengthValidatorForCharSequence.class,
    FixedLengthValidator.FixedLengthValidatorForArray.class,
    FixedLengthValidator.FixedLengthValidatorForCollection.class,
    FixedLengthValidator.FixedLengthValidatorForMap.class
})
public @interface FixedLength {

    int value();

    String message() default "{glz.hawkframework.validator.constraints.FixedLength.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Defines several {@link FixedLength} annotations on the same element.
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        FixedLength[] value();
    }

}
