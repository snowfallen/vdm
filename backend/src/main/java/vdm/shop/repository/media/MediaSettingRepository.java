package vdm.shop.repository.media;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vdm.shop.model.MediaSetting;

public interface MediaSettingRepository extends JpaRepository<MediaSetting, Long> {

    Optional<MediaSetting> findBySettingKey(String settingKey);
}
