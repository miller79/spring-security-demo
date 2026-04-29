package miller79.main;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.method.HandleAuthorizationDenied;

import tools.jackson.databind.annotation.JsonSerialize;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

/**
 * A data object that demonstrates field-level security — different fields are visible
 * or masked depending on the caller's permissions.
 *
 * <p>In many real applications, an API response might contain sensitive data (like a Social
 * Security Number or phone number) that should only be shown to users with the right
 * permissions. Instead of creating separate endpoints or DTOs, Spring Security lets you
 * protect individual getter methods with {@code @PreAuthorize}.
 *
 * <ul>
 *   <li>{@code name} — always visible to any authenticated user</li>
 *   <li>{@code ssn} — only visible to users with the {@code name:miller79} authority;
 *       otherwise returns "***"</li>
 *   <li>{@code phoneNumber} — only visible to users with the {@code name:miller79} authority;
 *       otherwise returns a partially masked value like "***-***-7890"</li>
 * </ul>
 *
 * <p>The masking behavior is handled by
 * {@link SemiSecretObjectMaskMethodAuthorizationDeniedHandler}.
 * Lombok's {@code @Builder} and {@code @RequiredArgsConstructor} generate the builder pattern
 * and constructor automatically.
 *
 * @see <a href=
 *      "https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html#_combining_with_meta_annotation_support">Method
 *      Security</a>
 */
@Builder
@RequiredArgsConstructor
@JsonSerialize(as = SemiSecretObject.class)
public class SemiSecretObject {
    private final String name;
    private final String ssn;
    private final String phoneNumber;

    /**
     * Returns the person's name — always visible to authenticated users.
     *
     * @return the person's name (no masking applied)
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the SSN if the caller has the {@code name:miller79} authority, otherwise a masked value.
     *
     * @return the Social Security Number, or "***" if the caller lacks authorization
     */
    @PreAuthorize("hasAuthority('name:miller79')")
    @HandleAuthorizationDenied(handlerClass = SemiSecretObjectMaskMethodAuthorizationDeniedHandler.class)
    public String getSsn() {
        return ssn;
    }

    /**
     * Returns the phone number if the caller has the {@code name:miller79} authority,
     * otherwise a partially masked value.
     *
     * @return the full phone number, or a masked value like "***-***-7890" showing only the last 4 digits
     */
    @PreAuthorize("hasAuthority('name:miller79')")
    @HandleAuthorizationDenied(handlerClass = SemiSecretObjectMaskMethodAuthorizationDeniedHandler.class)
    public String getPhoneNumber() {
        return phoneNumber;
    }
}
