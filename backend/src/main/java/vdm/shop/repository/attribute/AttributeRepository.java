package vdm.shop.repository.attribute;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vdm.shop.model.Attribute;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Long> {

    @Query("SELECT a FROM Attribute a LEFT JOIN FETCH a.unit")
    Page<Attribute> findAllWithUnit(Pageable pageable);

    @Query("SELECT a FROM Attribute a LEFT JOIN FETCH a.unit")
    List<Attribute> findAllWithUnit();
}
