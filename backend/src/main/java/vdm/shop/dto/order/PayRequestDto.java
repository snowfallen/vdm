package vdm.shop.dto.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayRequestDto {
    // Мок-платіж: просто симулюємо успішну оплату
    // В майбутньому тут буде токен від платіжного провайдера
    private String mockCardLast4; // останні 4 цифри картки (UI only)
}
