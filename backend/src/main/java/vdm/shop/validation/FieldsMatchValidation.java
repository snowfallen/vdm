package vdm.shop.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import org.springframework.beans.BeanWrapperImpl;

public class FieldsMatchValidation implements ConstraintValidator<FieldMatch, Object> {
    private String field;
    private String repeatField;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.repeatField = constraintAnnotation.repeatField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        Object fieldValue = new BeanWrapperImpl(value).getPropertyValue(field);
        Object repeatFieldValue = new BeanWrapperImpl(value).getPropertyValue(repeatField);
        return Objects.equals(fieldValue, repeatFieldValue);
    }
}
