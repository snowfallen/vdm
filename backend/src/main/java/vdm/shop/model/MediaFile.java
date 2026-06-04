package vdm.shop.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "media_files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Ключ групи: "slider" | "certificates" | "about"
     */
    @Column(name = "file_key", nullable = false)
    private String fileKey;

    /**
     * Шлях об'єкту в MinIO bucket: "slider/uuid.webp"
     */
    @Column(name = "object_name", nullable = false, length = 500)
    private String objectName;

    /**
     * Публічна URL для клієнта
     */
    @Column(nullable = false, length = 1000)
    private String url;

    @Column(name = "original_name")
    private String originalName;

    @Column(name = "sort_order")
    private Integer sortOrder;

    private String title;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.sortOrder == null) {
            this.sortOrder = 0;
        }
    }
}
