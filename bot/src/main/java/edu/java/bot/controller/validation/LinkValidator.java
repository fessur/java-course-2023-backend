package edu.java.bot.controller.validation;

import edu.java.bot.controller.validation.annotation.CorrectLink;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.springframework.stereotype.Component;

@Component
public class LinkValidator implements ConstraintValidator<CorrectLink, String> {

    @Override
    public void initialize(CorrectLink constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String link, ConstraintValidatorContext constraintValidatorContext) {
        try {
            URL parsed = new URI(link).toURL();
            return true;
        } catch (MalformedURLException | URISyntaxException | IllegalArgumentException ex) {
            return false;
        }
    }
}
