import { Injectable } from '@angular/core';
import { jwtDecode } from 'jwt-decode';

interface JwtPayload {
  sub: string
  role: string
  iar: number
  exp: number
}

@Injectable({
  providedIn: 'root'
})


export class Token {
  getToken(): string | null {
    return localStorage.getItem('token')
  }

  decodeToken(): JwtPayload | null {
    const token = this.getToken()
    if (!token) {
      return null
    }

    try {
      return jwtDecode<JwtPayload>(token)
    } catch (error) {
      return null
    }
  }

  getUsername(): string | null {
    const decode = this.decodeToken()
    return decode ? decode.sub : null
  }

  getRole(): string | null {
    const decode = this.decodeToken()
    return decode ? decode.role : null
  }
  
  getExpired(): boolean {
    const decode = this.decodeToken()
    if (!decode) {
      return true
    }
    return Date.now() / 1000 > decode.exp
  }

  logout() {
    localStorage.removeItem('token')
  }
}
