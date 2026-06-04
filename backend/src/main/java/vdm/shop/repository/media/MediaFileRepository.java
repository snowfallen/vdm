package vdm.shop.repository.media;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vdm.shop.model.MediaFile;

public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {

    List<MediaFile> findByFileKeyOrderBySortOrderAsc(String fileKey);

    @Modifying
    @Query("DELETE FROM MediaFile f WHERE f.fileKey = :fileKey")
    void deleteAllByFileKey(@Param("fileKey") String fileKey);
}
