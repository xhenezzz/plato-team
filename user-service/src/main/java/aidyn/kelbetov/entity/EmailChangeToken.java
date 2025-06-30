package aidyn.kelbetov.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailChangeToken {
    @Id
    private String token;

    private String newEmail;
    private String oldEmail;

    private LocalDateTime expiry;
}
