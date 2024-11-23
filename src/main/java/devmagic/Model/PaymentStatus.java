package devmagic.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public enum PaymentStatus {
    PENDING(0, "Pending"),
    PROCESSING(1, "Processing"),
    COMPLETED(2, "Completed"),
    CANCELLED(3, "Cancelled");

    private final int code;
    private final String description;

    PaymentStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    // Phương thức tiện ích để chuyển từ mã trạng thái sang enum
    public static PaymentStatus fromCode(int code) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status code: " + code);
    }
}


