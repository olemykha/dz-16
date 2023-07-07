import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class BookingDates {
    private LocalDate checkin;
    private LocalDate checkout;

}
