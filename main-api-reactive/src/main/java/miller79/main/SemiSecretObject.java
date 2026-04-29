package miller79.main;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * A data object representing a person with potentially sensitive fields.
 *
 * <p>In the reactive module, this is a simple POJO (Plain Old Java Object) with no
 * security annotations on its getters. Unlike the servlet module's version (which uses
 * {@code @PreAuthorize} on individual getter methods), field-level masking is handled
 * in the controller before this object is constructed.
 *
 * <p>Lombok generates the getter methods ({@code @Getter}), the builder pattern ({@code @Builder}),
 * and the all-args constructor ({@code @RequiredArgsConstructor}) automatically.
 */
@Getter
@Builder
@RequiredArgsConstructor
class SemiSecretObject {
    private final String name;
    private final String ssn;
    private final String phoneNumber;
}
