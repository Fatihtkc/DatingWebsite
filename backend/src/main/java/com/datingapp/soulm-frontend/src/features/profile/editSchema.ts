import { z } from 'zod';
import { nameField, profileSchema } from '@/features/auth/schemas';

/** The sign-up profile fields plus the two names, which can be edited here. */
export const editProfileSchema = profileSchema.extend({
  firstName: nameField('first name'),
  lastName: nameField('last name'),
});
export type EditProfileValues = z.infer<typeof editProfileSchema>;
