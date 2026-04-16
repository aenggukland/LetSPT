package com.aenggukland.letspt.fcm;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fcm")
@RequiredArgsConstructor
public class FcmTokenController {
    private final FcmTokenService fcmTokenService;

    @PostMapping
    public ResponseEntity<Void> saveFcmToken(@RequestAttribute("username") String username, @RequestBody @Valid FcmSaveRequest fcmSaveRequest){
        fcmTokenService.saveFcmToken(username, fcmSaveRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{deviceId}")
    public ResponseEntity<Void> deleteFcmToken(@RequestAttribute("username") String username, @PathVariable String deviceId){
        fcmTokenService.deleteFcmToken(username, deviceId);
        return ResponseEntity.ok().build();
    }
}
