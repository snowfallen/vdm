package vdm.shop.dto.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceOrderRequestDto {
    // Можна залишити порожнім — тоді береться адреса з профілю клієнта
    private String deliveryCountry;
    private String deliveryCity;
    private String deliveryStreet;
    private String deliveryHouse;
    private String deliveryApartment;
    private String deliveryPostalCode;
    private String comment;
}
