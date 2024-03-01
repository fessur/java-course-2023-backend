package edu.java.controller.validation;

import edu.java.controller.validation.annotation.SupportedLink;
import edu.java.service.LinkService;
import edu.java.util.CommonUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.springframework.stereotype.Component;

@Component
public class LinkValidator implements ConstraintValidator<SupportedLink, String> {
    private final LinkService linkService;

    public LinkValidator(LinkService linkService) {
        this.linkService = linkService;
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
            if (!linkService.isSupported(parsed.getHost())) {
                constraintValidatorContext.buildConstraintViolationWithTemplate(
                    "Domain " + parsed.getHost() + " is not supported yet. List of all supported domains:\n"
                        + CommonUtils.joinEnumerated(linkService.getSupportedDomains(), 1)).addConstraintViolation();
                return false;
            }
            return true;
        } catch (MalformedURLException | URISyntaxException | IllegalArgumentException ex) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("The link is not correct")
                .addConstraintViolation();
            return false;
        }
    }
}
