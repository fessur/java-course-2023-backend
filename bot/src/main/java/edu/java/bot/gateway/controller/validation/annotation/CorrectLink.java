package edu.java.bot.gateway.controller.validation.annotation;

import edu.java.bot.gateway.controller.validation.LinkValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = LinkValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CorrectLink {
    String message() default "The link is not correct";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
