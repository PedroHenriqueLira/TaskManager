import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

function isBrowser(): boolean {
  return typeof window !== 'undefined' && typeof document !== 'undefined';
}

function getCookie(name: string): string | null {
  if (!isBrowser()) return null;
  const nameEQ = name + '=';
  const ca = document.cookie.split(';');
  for (let c of ca) {
    c = c.trim();
    if (c.indexOf(nameEQ) === 0) return c.substring(nameEQ.length);
  }
  return null;
}

export const loginGuard: CanActivateFn = () => {
  const router = inject(Router);

  const token = getCookie('token');
  if (token) {
    return router.parseUrl('/dashboard');
  }

  return true;
};
