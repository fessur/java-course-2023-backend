package edu.java.controller.validation;

import edu.java.client.GithubClient;
import edu.java.client.StackOverflowClient;
import edu.java.controller.validation.annotation.SupportedLink;
import edu.java.service.LinkService;
import edu.java.util.CommonUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class LinkValidator implements ConstraintValidator<SupportedLink, String> {
    private static final List<String> SUPPORTED_DOMAINS = List.of("github.com", "stackoverflow.com");
    private final GithubClient githubClient;
    private final StackOverflowClient stackOverflowClient;

    public LinkValidator(GithubClient githubClient, StackOverflowClient stackOverflowClient) {
        this.githubClient = githubClient;
        this.stackOverflowClient = stackOverflowClient;
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
            if (isGithubRepository(parsed)) {
                if (githubClient.exists(LinkService.toGithubRepository(parsed))) {
                    return true;
                }
                constraintValidatorContext.buildConstraintViolationWithTemplate("Cannot find such repository.");
                return false;
            }
            if (isStackOverflowQuestion(parsed)) {
                if (stackOverflowClient.exists(LinkService.toStackOverflowQuestion(parsed))) {
                    return true;
                }
                constraintValidatorContext.buildConstraintViolationWithTemplate("Cannot find such question.");
                return false;
            }
            constraintValidatorContext.buildConstraintViolationWithTemplate(
                "Domain " + parsed.getHost() + " is not supported yet. List of all supported domains:\n"
                    + CommonUtils.joinEnumerated(SUPPORTED_DOMAINS, 1)).addConstraintViolation();
            return false;
        } catch (MalformedURLException | URISyntaxException | IllegalArgumentException ex) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("The link is not correct")
                .addConstraintViolation();
            return false;
        }
    }

    private boolean isGithubRepository(URL url) {
        String[] parts = url.getPath().split("/");
        return url.getProtocol().equals("https")
            && url.getHost().equals("github.com")
            && parts.length >= 2
            && !parts[1].isEmpty()
            && !parts[2].isEmpty();
    }

    private boolean isStackOverflowQuestion(URL url) {
        if (!url.getProtocol().equals("https") || !url.getHost().equals("stackoverflow.com")) {
            return false;
        }

        String[] parts = url.getPath().split("/");
        if (parts.length < 2 || !parts[1].equals("questions")) {
            return false;
        }

        try {
            Long.parseLong(parts[2]);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
