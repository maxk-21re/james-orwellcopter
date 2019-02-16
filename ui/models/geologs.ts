export interface LatLon {
  latitude: number
  longitude: number
}

export interface Geolog {
  location: LatLon
  accuracy:	number
  timestamp: string
  userId: number
  id: number
}
