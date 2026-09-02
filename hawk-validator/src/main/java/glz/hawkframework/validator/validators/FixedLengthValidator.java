package glz.hawkframework.validator.validators;

import glz.hawkframework.validator.constraints.FixedLength;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.Map;

/**
 * This class is responsible for validating the object annotated by {@link FixedLength}
 *
 * @author Hawk
 */
public class FixedLengthValidator<T> implements ConstraintValidator<FixedLength, T> {

    protected int length;

    @Override
    public void initialize(FixedLength constraintAnnotation) {
        this.length = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(T value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return check(value);
    }

    protected boolean check(T value) {
        return false;
    }

    public static class FixedLengthValidatorForCharSequence extends FixedLengthValidator<CharSequence> {
        @Override
        protected boolean check(CharSequence value) {
            return value.length() == length;
        }
    }

    public static class FixedLengthValidatorForArray extends FixedLengthValidator<Object[]> {
        @Override
        protected boolean check(Object[] value) {
            return value.length == length;
        }
    }

    public static class FixedLengthValidatorForCollection extends FixedLengthValidator<Collection<?>> {
        @Override
        protected boolean check(Collection<?> value) {
            return value.size() == length;
        }
    }

    public static class FixedLengthValidatorForMap extends FixedLengthValidator<Map<?, ?>> {
        @Override
        protected boolean check(Map<?, ?> value) {
            return value.size() == length;
        }
    }

}
