import { MediaResponse } from "./mediaResponse";


export interface PostResponse {
    id: number;
    title: string
    content: string
    authorUsername: string
    createdAt: string
    totalLikes: number
    totalComments: number
    LikedByCurrentUser: boolean
    hided: boolean
    media: MediaResponse[]
}