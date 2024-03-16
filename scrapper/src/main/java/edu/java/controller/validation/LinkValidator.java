package edu.java.controller.validation;

import edu.java.controller.validation.annotation.SupportedLink;
import edu.java.service.LinkUpdaterService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class LinkValidator implements ConstraintValidator<SupportedLink, String> {
    private final LinkUpdaterService linkUpdaterService;

    public LinkValidator(LinkUpdaterService linkUpdaterService) {
        this.linkUpdaterService = linkUpdaterService;
    }

    @Override
    public void initialize(SupportedLink constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String link, ConstraintValidatorContext constraintValidatorContext) {
        constraintValidatorContext.disableDefaultConstraintViolation();
        try {
            URL parsed = new URI(link).toURL();
            Optional<String> validationMessage = linkUpdaterService.validateLink(parsed);
            if (validationMessage.isPresent()) {
                constraintValidatorContext.buildConstraintViolationWithTemplate(validationMessage.get())
                    .addConstraintViolation();
                return false;
            } else {
                return true;
            }
        } catch (MalformedURLException | URISyntaxException | IllegalArgumentException ex) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("The link is not correct")
                .addConstraintViolation();
            return false;
        }
    }
}
