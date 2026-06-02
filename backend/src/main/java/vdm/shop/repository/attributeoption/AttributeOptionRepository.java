package vdm.shop.repository.attributeoption;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vdm.shop.model.AttributeOption;

@Repository
public interface AttributeOptionRepository extends JpaRepository<AttributeOption, Long> {

    @Query("SELECT o FROM AttributeOption o JOIN FETCH o.attribute "
            + "WHERE o.attribute.id = :attributeId")
    List<AttributeOption> findAllByAttributeId(Long attributeId);
}
