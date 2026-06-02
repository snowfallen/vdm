import {IUserRegistrationData} from './i-user-registration-data';
import {IClientProfileData} from './i-client-profile-data';


export interface IClientRegistrationRequest {
  userRegistrationData: IUserRegistrationData;
  clientData: IClientProfileData;
}
