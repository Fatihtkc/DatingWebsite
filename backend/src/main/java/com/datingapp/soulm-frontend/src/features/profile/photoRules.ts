/** Limits enforced by FileStorageServiceImpl; checking them first saves a round trip. The server stays the authority. */
export const MAX_PHOTOS = 10;
export const MAX_PHOTO_BYTES = 5 * 1024 * 1024;
export const MAX_TOTAL_BYTES = 25 * 1024 * 1024;
const ALLOWED_TYPES: readonly string[] = ['image/jpeg', 'image/png'];

export interface PhotoLike {
  name: string;
  size: number;
  type: string;
}

/** Returns a message per problem; an empty array means the selection can be uploaded. */
export function checkPhotos(files: readonly PhotoLike[], maxCount: number = MAX_PHOTOS): string[] {
  const problems: string[] = [];

  if (files.length > maxCount) problems.push(`You can upload up to ${maxCount} photos at once.`);

  for (const file of files) {
    if (!ALLOWED_TYPES.includes(file.type)) problems.push(`${file.name} isn't a JPG or PNG image.`);
    else if (file.size > MAX_PHOTO_BYTES) problems.push(`${file.name} is larger than 5 MB.`);
  }

  const total = files.reduce((sum, file) => sum + file.size, 0);
  if (total > MAX_TOTAL_BYTES) problems.push('Together, these photos are larger than 25 MB.');

  return problems;
}
