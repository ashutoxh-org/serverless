package dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublishedRequestDTO {
    private String email;

    @Override
    public String toString() {
        return "{" +
                "\"email\":\"" + email +
                "\"}";
    }
}