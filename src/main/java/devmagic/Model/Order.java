
    package devmagic.Model;

    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.util.Date;
    import java.util.List;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Entity
    @Table(name = "Orders")
    public class Order {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int orderId;

        @ManyToOne
        @JoinColumn(name = "account_id", nullable = false)
        private Account account;

        @Column(name = "order_date", nullable = false)
        private Date orderDate;

        //    @Enumerated(EnumType.ORDINAL)
        @Column(name = "payment_status", nullable = false)
        private String paymentStatus;

        @Column(name = "payment_method", nullable = false)
        private String paymentMethod;

        @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<OrderDetail> orderDetails;

        @Column(name = "is_deleted", nullable = false)
        private boolean isDeleted = false;

        @Column(name = "note", columnDefinition = "NVARCHAR(MAX)")
        private String note;
    }
