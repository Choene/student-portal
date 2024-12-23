import { HttpInterceptorFn } from '@angular/common/http';

  // TODO: Implement the JWT interceptor
  // Retrieve the JWT token from localStorage
  // Check if the token exists
  // Clone the request and add the Authorization header
  // Attach the JWT token
  // Pass the cloned request to the next handler
  // If no token, pass the request unmodified
export const jwtInterceptor: HttpInterceptorFn = (req, next) => {

  const token = localStorage.getItem('token');

  if (token) {

    const clonedRequest = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });

    return next(clonedRequest);
  }

  return next(req);
};
