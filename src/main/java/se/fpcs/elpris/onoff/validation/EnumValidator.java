package se.fpcs.elpris.onoff.validation;

import java.util.Arrays;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<ValidEnum, Object> {

  private Class<? extends Enum<?>> enumClass;
  private boolean allowNull;

  @Override
  public void initialize(ValidEnum annotation) {
    this.enumClass = annotation.enumClass();
    this.allowNull = annotation.allowNull();
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    if (value == null) {
      return allowNull;
    }

    if (!enumClass.isEnum()) {
      throw new IllegalArgumentException("Provided class is not an enum type");
    }

    Object[] enumValues = enumClass.getEnumConstants();
    return Arrays.stream(enumValues)
        .anyMatch(enumValue -> enumValue.toString().equals(value.toString()));
  }
}
