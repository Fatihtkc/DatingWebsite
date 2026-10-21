import { describe, expect, it } from 'vitest';
import { checkPhotos, MAX_PHOTO_BYTES } from './photoRules';

const photo = (name: string, size = 1000, type = 'image/jpeg') => ({ name, size, type });

describe('checkPhotos', () => {
  it('accepts JPG and PNG within the limits', () => {
    expect(checkPhotos([photo('a.jpg'), photo('b.png', 2000, 'image/png')])).toEqual([]);
  });

  it('rejects other types and oversized files by name', () => {
    expect(checkPhotos([photo('a.gif', 10, 'image/gif')])).toEqual(["a.gif isn't a JPG or PNG image."]);
    expect(checkPhotos([photo('big.jpg', MAX_PHOTO_BYTES + 1)])).toEqual(['big.jpg is larger than 5 MB.']);
  });

  it('limits the count and the combined size', () => {
    const eleven = Array.from({ length: 11 }, (_, i) => photo(`${i}.jpg`));
    expect(checkPhotos(eleven)).toContain('You can upload up to 10 photos at once.');
    const heavy = Array.from({ length: 6 }, (_, i) => photo(`${i}.jpg`, MAX_PHOTO_BYTES));
    expect(checkPhotos(heavy)).toContain('Together, these photos are larger than 25 MB.');
  });
});
