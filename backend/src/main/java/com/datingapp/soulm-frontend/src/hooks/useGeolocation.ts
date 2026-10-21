import { useCallback, useState } from 'react';

export type LocationStatus = 'idle' | 'requesting' | 'granted' | 'denied' | 'unavailable' | 'timeout' | 'unsupported';

export interface Coordinates {
  latitude: number;
  longitude: number;
}

/** Three decimals is roughly 110 m: plenty for "people within 0.1-100 km", and no need to store more. */
const round = (value: number) => Math.round(value * 1000) / 1000;

export interface GeolocationControl {
  status: LocationStatus;
  coords: Coordinates | null;
  request: () => void;
}

/** Wraps the browser Geolocation API. Never assumes permission; every outcome has a status. */
export function useGeolocation(): GeolocationControl {
  const supported = typeof navigator !== 'undefined' && 'geolocation' in navigator;
  const [status, setStatus] = useState<LocationStatus>(supported ? 'idle' : 'unsupported');
  const [coords, setCoords] = useState<Coordinates | null>(null);

  const request = useCallback(() => {
    if (!supported) {
      setStatus('unsupported');
      return;
    }
    setStatus('requesting');
    navigator.geolocation.getCurrentPosition(
      (position) => {
        setCoords({ latitude: round(position.coords.latitude), longitude: round(position.coords.longitude) });
        setStatus('granted');
      },
      (error) => {
        setCoords(null);
        if (error.code === error.PERMISSION_DENIED) setStatus('denied');
        else if (error.code === error.TIMEOUT) setStatus('timeout');
        else setStatus('unavailable');
      },
      { enableHighAccuracy: false, timeout: 15_000, maximumAge: 10 * 60_000 },
    );
  }, [supported]);

  return { status, coords, request };
}
