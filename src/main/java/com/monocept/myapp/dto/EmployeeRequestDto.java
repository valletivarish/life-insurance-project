package com.monocept.myapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmployeeRequestDto {

    private long employeeId;

    @NotBlank(message = "Username is required.")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters.")
    private String username;

    @NotBlank(message = "First name is required.")
    @Size(min = 2, max = 50, message = "First name must be between 1 and 50 characters.")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Size(min = 2, max = 50, message = "Last name must be between 1 and 50 characters.")
    private String lastName;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email should be valid.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 8,  message = "Password must be at least 8 characters long.")
    private String password;

    @NotNull(message = "Salary is required.")
    @Min(value = 0, message = "Salary must be a positive number.")
    private double salary;

    private boolean active;
}
