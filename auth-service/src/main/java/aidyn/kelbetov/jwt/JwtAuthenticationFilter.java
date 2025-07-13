//package aidyn.kelbetov.jwt;
//
//import aidyn.kelbetov.model.Role;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.List;
//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//    private final JwtTokenProvider tokenProvider;
//
//
//    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
//        this.tokenProvider = tokenProvider;
//    }
//
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//        String authHeader = request.getHeader("Authorization");
//
//        if(authHeader != null && authHeader.startsWith("Bearer ")){
//            String token = authHeader.substring(7);
//
//            if(tokenProvider.validateToken(token)){
//                String email = tokenProvider.getEmailFromToken(token);
//                Role role = tokenProvider.getRoleFromToken(token);
//                UsernamePasswordAuthenticationToken auth =
//                        new UsernamePasswordAuthenticationToken(email, null, List.of(new SimpleGrantedAuthority(role.name())));
//
//                SecurityContextHolder.getContext().setAuthentication(auth);
//            }
//        }
//        filterChain.doFilter(request, response);
//    }
//}
