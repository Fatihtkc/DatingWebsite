import type { ImageDto } from './social';
import type { Alcohol, BodyType, Diet, Gender, RelationshipType, Smoking } from './enums';

/**
 * Body of POST /api/users/signup (UserCreateDTO). Optional properties are omitted when empty:
 * Jackson rejects "" for enums and numbers.
 */
export interface UserCreateRequest {
  username: string; // 3-30
  firstName: string; // 2-50
  lastName: string; // 2-50
  email: string; // valid, <=254
  password: string; // 12-128, upper + lower + digit
  birthDate?: string | undefined; // ISO yyyy-MM-dd, must be in the past (the backend does not enforce 18+)
  gender: Gender; // required
  height?: number | undefined; // 50-250
  location?: string | undefined; // <=500
  shorterBio?: string | undefined; // <=500
  hobbies?: string | undefined; // <=1000
  favoriteMusic?: string | undefined; // <=200
  weekendPlans?: string | undefined; // <=500
  bodyType?: BodyType | undefined;
  relationshipType?: RelationshipType | undefined;
  agePreference?: string | undefined; // free text, <=500
  distancePreference?: string | undefined; // free text, <=500
  smoke?: Smoking | undefined;
  alcohol?: Alcohol | undefined;
  diet?: Diet | undefined;
  latitude: number; // required, -90..90
  longitude: number; // required, -180..180
}

/** Properties UserMapper#toDTO fills in. `images` holds approved images only. */
export interface UserProfile {
  id: number;
  username: string;
  firstName: string | null;
  lastName: string | null;
  birthDate: string | null;
  gender: Gender | null;
  height: number | null;
  bodyType: BodyType | null;
  location: string | null;
  relationshipType: RelationshipType | null;
  alcohol: Alcohol | null;
  smoke: Smoking | null;
  diet: Diet | null;
  shorterBio: string | null;
  hobbies: string | null;
  favoriteMusic: string | null;
  weekendPlans: string | null;
  images: ImageDto[] | null;
}

/**
 * Body of PUT /api/users/profile/{id} (UserUpdateDTO). The server OVERWRITES every field with what is
 * sent, so anything left out becomes null. `latitude` and `longitude` are @NotNull. `username` is in the
 * DTO but ignored by the service, so it is not sent.
 */
export interface UserUpdateRequest {
  firstName?: string | undefined;
  lastName?: string | undefined;
  birthDate?: string | undefined;
  gender?: Gender | undefined;
  height?: number | undefined;
  bodyType?: BodyType | undefined;
  location?: string | undefined;
  relationshipType?: RelationshipType | undefined;
  agePreference?: string | undefined;
  distancePreference?: string | undefined;
  smoke?: Smoking | undefined;
  alcohol?: Alcohol | undefined;
  diet?: Diet | undefined;
  shorterBio?: string | undefined;
  hobbies?: string | undefined;
  favoriteMusic?: string | undefined;
  weekendPlans?: string | undefined;
  latitude: number;
  longitude: number;
}
