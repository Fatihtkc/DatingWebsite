package com.datingapp.backend.websocket;

import com.datingapp.backend.repository.ConversationRepository;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.security.JwtUtil;
import com.datingapp.backend.service.impl.CustomUserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor 
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private static final Pattern CONVERSATION_TOPIC = Pattern.compile("^/topic/conversations/(\\d+)$");

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsServiceImpl userDetailsService;
    private final ConversationRepository conversationRepository;
    private final WebSocketRateLimiter webSocketRateLimiter;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel){

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command)){
            authenticate(accessor);
        }

        if (StompCommand.SUBSCRIBE.equals(command)){
            authorizeSubscription(accessor);
        }

        if (StompCommand.SEND.equals(command)){
            authorizeSend(accessor);
        }

        return message;
    }

    private void authenticate(StompHeaderAccessor accessor){

        String authHeader = accessor.getFirstNativeHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            throw new IllegalArgumentException("Authorization header is missing or invalid.");
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateJwtToken(token)){
            throw new IllegalArgumentException("Invalid or expired token.");
        }

        String username = jwtUtil.getUsernameFromJwtToken(token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken( userDetails, null, userDetails.getAuthorities());

        accessor.setUser(authentication);
    }

    private void authorizeSubscription(StompHeaderAccessor accessor){

        if (accessor.getUser() == null){
            throw new IllegalArgumentException("Authentication required.");
        }

        String destination = accessor.getDestination();

        if (destination == null){
            throw new IllegalArgumentException("Subscription destination is required.");
        }

        Matcher matcher = CONVERSATION_TOPIC.matcher(destination);

        if (!matcher.matches()){
            throw new IllegalArgumentException("Invalid subscription destination.");
        }

        Long conversationId = Long.valueOf(matcher.group(1));

        Authentication authentication = (Authentication) accessor.getUser();

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

        boolean isMember = conversationRepository.existsByIdAndUserId(conversationId, principal.getId());

        if (!isMember){
            throw new IllegalArgumentException("You are not a member of this conversation.");
        }
    }

    private void authorizeSend(StompHeaderAccessor accessor){

        if (accessor.getUser() == null){
            throw new IllegalArgumentException("Authentication required.");
        }

        String destination = accessor.getDestination();

        if (!"/app/sendMessage".equals(destination)){
            throw new IllegalArgumentException("Invalid message destination.");
        }

        Authentication authentication = (Authentication) accessor.getUser();

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

        if (!webSocketRateLimiter.isAllowed(principal.getId())){
            throw new IllegalArgumentException("Too many messages. Please try again later.");
        }
    }
}