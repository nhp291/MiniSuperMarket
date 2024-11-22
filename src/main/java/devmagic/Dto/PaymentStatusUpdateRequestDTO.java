package devmagic.Dto;

import devmagic.Model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentStatusUpdateRequestDTO {
    private int orderId;
    private String paymentStatus;  // Sử dụng String thay vì PaymentStatus enum
}

