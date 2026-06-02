export interface IClientProfileData {
  country: string;
  city: string;
  street: string;
  houseNumber: string;
  apartmentNumber?: string; // Зробимо необов'язковим, якщо квартира не завжди є
  postalCode: string;
}
