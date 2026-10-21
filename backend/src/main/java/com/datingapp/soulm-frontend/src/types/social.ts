import type { Alcohol, BodyType, Diet, Gender, ModerationStatus, RelationshipType, Smoking } from './enums';
import type { UserProfile } from './user';

/** com.datingapp.backend.dto.ImageDTO. `imageUrl` is a relative path such as /uploads/<uuid>.jpg. */
export interface ImageDto {
  id: number;
  imageUrl: string;
  moderationStatus: ModerationStatus | null;
}

/** com.datingapp.backend.dto.User.NearbyUserDTO. `distance` is in kilometres. */
export interface NearbyUser {
  id: number;
  username: string;
  firstName: string | null;
  lastName: string | null;
  birthDate: string | null;
  gender: Gender | null;
  height: number | null;
  bodyType: BodyType | null;
  location: string | null;
  distance: number | null;
  relationshipType: RelationshipType | null;
  alcohol: Alcohol | null;
  smoke: Smoking | null;
  diet: Diet | null;
  shorterBio: string | null;
  hobbies: string | null;
  favoriteMusic: string | null;
  weekendPlans: string | null;
  /** May be null or empty: the mapper that fills this DTO did not populate images in the version supplied. */
  images: ImageDto[] | null;
}

/** Like.UserLikeDTO. `id` is the like's id, which is what DELETE /api/likes/{id} needs. */
export interface UserLike {
  id: number;
  likedUser: UserProfile;
  likedAt: string;
}

/** Match.MatchDTO. `user` is the other person. */
export interface Match {
  id: number;
  user: UserProfile;
  matchedAt: string;
}

/** Message.GetMessageDTO. `sentAt` is a LocalDateTime without a zone. */
export interface ChatMessage {
  id: number;
  conversationId: number;
  senderId: number;
  content: string;
  sentAt: string;
  isImage: boolean | null;
  isRead: boolean | null;
}

/** Message.SendMessageDTO. All three fields are @NotNull/@NotBlank; content is at most 2000 characters. */
export interface SendMessageRequest {
  conversationId: number;
  content: string;
  isImage: boolean;
}

/** Complaint.ComplaintCreateDTO: complainedId required, reason 10-1000 characters. */
export interface ComplaintCreateRequest {
  complainedId: number;
  reason: string;
}
