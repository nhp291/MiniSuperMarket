package devmagic.Model;

import java.util.List;

public class CartDto {
    private List<CartItemDto> items;
    private String paymentMethod;
    private String customerName;
    private String customerAddress;

    // Constructor, getters and setters
    public CartDto(List<CartItemDto> items, String paymentMethod, String customerName, String customerAddress) {
        this.items = items;
        this.paymentMethod = paymentMethod;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
    }

    public List<CartItemDto> getItems() {
        return items;
    }

    public void setItems(List<CartItemDto> items) {
        this.items = items;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }
}
