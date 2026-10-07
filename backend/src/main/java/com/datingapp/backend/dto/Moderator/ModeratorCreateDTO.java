package com.datingapp.backend.dto.Moderator;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
public class ModeratorCreateDTO{

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email cannot be left blank")
    @Email(message = "Please enter a valid email address")
    @Size(max = 254)
    private String email;

    @NotBlank(message = "Password cannot be left blank")
    @Size(min = 12, max = 128, message = "Password must be between 12 and 128 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{12,128}$",
        message = "The password must contain at least one uppercase letter, one lowercase letter, and one digit"
    )
    private String password;

    @Past(message = "Date of birth must be a past date")
    private LocalDate birthDate;

    @PastOrPresent(message = "The start date cannot be a future date")
    private LocalDate startDate;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Enter a valid phone number")
    private String phone;

    @Size(max = 500, message = "Image URL can be at most 500 characters long")
    private String imageUrl;
    
}
