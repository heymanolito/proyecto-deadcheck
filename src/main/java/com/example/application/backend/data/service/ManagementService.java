package com.example.application.backend.data.service;

import com.example.application.backend.data.entity.Status;
import com.example.application.backend.data.entity.Tracking;
import com.example.application.backend.data.entity.User;
import com.example.application.backend.data.exception.ParteNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ManagementService {

    private static final Logger log = LoggerFactory.getLogger(BreakServiceImpl.class);
    private final UserService userService;
    private final TrackingServiceImpl trackingService;

    @Autowired
    public ManagementService(UserService userService, TrackingServiceImpl trackingService) {
        this.userService = userService;
        this.trackingService = trackingService;
    }

    public void checkIn(boolean isWorking) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            if (currentUser != null) {
                modifyUserStatus(currentUser);
            } else {
                throw new UsernameNotFoundException("El usuario al que intenta añadir el parte no existe");
            }
        } catch (UsernameNotFoundException ex) {
            log.info("El usuario no existe", ex);
        }
    }

    private void modifyUserStatus(User currentUser) {
        try {
            List<Tracking> listOfPartes = currentUser.getTrackingList().isEmpty() ? new ArrayList<>() : currentUser.getTrackingList();
            Tracking tracking = getTrackingByUser(currentUser);
            if (canCheckIn(currentUser)) {
                tracking.setWorkCheckIn(LocalDateTime.now());
                tracking.setWorkCheckOut(null);
                tracking.setUser(currentUser);
                listOfPartes.add(tracking);
                currentUser.setTrackingList(listOfPartes);
                currentUser.setStatus(Status.Entrada);
                userService.update(currentUser);
            } else if (canCheckOut(currentUser)) {
                tracking.setWorkCheckOut(LocalDateTime.now());
                tracking.setUser(currentUser);
                listOfPartes.add(tracking);
                currentUser.setTrackingList(listOfPartes);
                currentUser.setStatus(Status.Salida);
                userService.update(currentUser);
            } else {
                throw new ParteNotFoundException("No se ha encontrado ningun parte");
            }
        } catch (ParteNotFoundException ex) {
            log.info("El usuario " + currentUser.getName() + "no tiene ninguna fecha de " + currentUser.getStatus(), ex);
        }

    }

    private Tracking getTrackingByUser(User currentUser) {
        Tracking tracking = trackingService.findLatestParteByUser(currentUser);
        if (tracking == null) {
            tracking = new Tracking();
        } else {
            tracking = trackingService.findLatestParteByUser(currentUser);
        }
        return tracking;
    }

    /**
     * Si el metodo de JPA para encontrar el ultimo parte no funciona,
     * implementar esta función
     * private Tracking lookForLatestParte(User currentUser) throws ParteNotFoundException {
     * <p>
     * <p>
     * return currentUser.getTrackingList().stream()
     * .filter(parte -> parte.getWorkCheckOut() == null)
     * .findFirst()
     * .orElseThrow(() -> new ParteNotFoundException("No se ha encontrado ningun parte"));
     * <p>
     * }
     */


    private boolean canCheckOut(User currentUser) {
        return currentUser.getTrackingList().stream().anyMatch(parte -> parte.getWorkCheckIn() != null && parte.getWorkCheckOut() == null) && currentUser.getStatus() == Status.Salida;

    }

    private boolean canCheckIn(User currentUser) {
        return currentUser.getTrackingList().stream().anyMatch(parte -> parte.getWorkCheckIn() == null && parte.getWorkCheckOut() == null) && currentUser.getStatus() == Status.Entrada;

    }

}
