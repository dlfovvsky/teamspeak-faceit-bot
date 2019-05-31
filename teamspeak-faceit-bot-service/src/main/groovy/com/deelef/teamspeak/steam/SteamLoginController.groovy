package com.deelef.teamspeak.steam

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by adam on 06.11.2016.
 */
@RestController
@RequestMapping("/steam")
@Slf4j
class SteamLoginController {

    @Autowired
    SteamService steamService

    @Value("\${steam.auth-callback}")
    String authCallbackURL

    @RequestMapping("/steam-auth")
    public void login(HttpServletResponse response) {
        SteamOpenID steamOpenID = new SteamOpenID();
        response.sendRedirect(steamOpenID.login(authCallbackURL));
    }

    @RequestMapping("/steam-auth-callback")
    public String callback(HttpServletRequest httpServletRequest, @RequestParam Map<String, String> allRequestParams) {
        SteamOpenID steamOpenID = new SteamOpenID();
        def steam64Id = steamOpenID.verify(httpServletRequest.getRequestURL().toString(), allRequestParams)
        log.info("remote addr: " + httpServletRequest.getRemoteAddr())
        log.info("remote host: " + httpServletRequest.getRemoteHost())
        log.info("local addr: " + httpServletRequest.getLocalAddr())
        log.info("X-Real-IP: " + httpServletRequest.getHeader("X-Real-IP"))

        return steamService.onAuth(httpServletRequest.getRemoteAddr(), steam64Id)
    }


    @GetMapping("/auth")
    public def auth(HttpServletRequest httpServletRequest, HttpServletResponse response, @RequestHeader("request-callback-url") String callbackURL) {
        SteamOpenID steamOpenID = new SteamOpenID();
        String URL = steamOpenID.login(callbackURL)
        def resp =[
                "URL": URL
        ]
        return resp;
    }

    @GetMapping("/auth-callback")
    public ResponseEntity authCallback(@RequestHeader("request-callback-url") String callbackURL, @RequestParam Map<String, String> allRequestParams) {
        SteamOpenID steamOpenID = new SteamOpenID();
        String steam64Id = steamOpenID.verify(callbackURL, allRequestParams)
        if(!steam64Id){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

//        User user = userRepository.findOneByLogin(steam64Id)
//            .map({
//            User user ->
//                return user
//            })
//            .orElseGet({
//                ->
//                return userService.createUser(RegistrationType.STEAM, steam64Id, steam64Id,
//                steam64Id, steam64Id, null,
//                "pl");
//            });
//        if(user){
//            CompletableFuture<SteamPlayerProfile> future = steamApi.getSteamPlayerProfile(user.login)
//            SteamPlayerProfile steamPlayerProfile = future.get()
//            log.info("Async save execut" + steamPlayerProfile.avatarUrl)
//            user.account.avatarURL = steamPlayerProfile.avatarUrl
//            user.firstName = steamPlayerProfile.name
//            userRepository.save(user)
//        }
//
//        SteamAuthenticationToken token = new SteamAuthenticationToken(steam64Id, null)
//        Authentication authentication = authenticationManager.authenticate(token)
//        if(!authentication.isAuthenticated()){
//            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
//        }
//        SecurityContextHolder.getContext().setAuthentication(authentication)
//        log.info("STAEM CURRENT USER: " + SecurityUtils.getCurrentUserLogin())

        return new ResponseEntity(HttpStatus.OK);
    }


    @RequestMapping("/steam-auth-redirect")
    public void redirect(HttpServletRequest request, HttpServletResponse response) {
        response.sendRedirect("http://localhost:9000/#/steam/auth-callback?" + request.getQueryString())
    }

}
