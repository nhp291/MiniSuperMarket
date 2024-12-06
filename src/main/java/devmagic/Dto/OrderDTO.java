package devmagic.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    private int orderId;
    private String username;
    private Date orderDate;
    private String paymentStatus;
    private String paymentMethod;
    private List<OrderDetailDTO> orderDetails;

}