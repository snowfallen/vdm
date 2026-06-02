package vdm.shop.repository.unit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vdm.shop.model.Unit;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {
}
